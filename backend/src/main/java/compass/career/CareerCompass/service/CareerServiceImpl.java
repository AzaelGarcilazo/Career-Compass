package compass.career.CareerCompass.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import compass.career.CareerCompass.dto.*;
import compass.career.CareerCompass.mapper.AdminMapper;
import compass.career.CareerCompass.mapper.CareerMapper;
import compass.career.CareerCompass.model.*;
import compass.career.CareerCompass.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CareerServiceImpl implements CareerService {

    private final CareerRepository careerRepository;
    private final CareerRecommendationRepository careerRecommendationRepository;
    private final UserRepository userRepository;
    private final CompletedEvaluationRepository completedEvaluationRepository;
    private final SocialMediaApiService socialMediaApiService;
    private final GroqService groqService;
    private final ObjectMapper objectMapper;

    // Caché simple de recomendaciones (1 hora)
    private final Map<Integer, CachedRecommendations> cache = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public List<CareerRecommendationResponse> getRecommendedCareers(Integer userId) {
        log.info("Generating career recommendations for user {}", userId);

        // Verificar caché
        CachedRecommendations cached = cache.get(userId);
        if (cached != null && !cached.isExpired()) {
            log.info("Returning cached recommendations for user {}", userId);
            return cached.getRecommendations();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Verificar si ya existen recomendaciones en BD (generadas previamente)
        List<CareerRecommendation> existingRecommendations =
                careerRecommendationRepository.findByUserIdOrderByCompatibilityPercentageDesc(userId);

        if (!existingRecommendations.isEmpty()) {
            log.info("Found {} existing recommendations in database for user {}", existingRecommendations.size(), userId);
            List<CareerRecommendationResponse> responses = existingRecommendations.stream()
                    .map(CareerMapper::toRecommendationResponse)
                    .collect(Collectors.toList());

            // Guardar en caché
            cache.put(userId, new CachedRecommendations(responses));
            return responses;
        }

        // Generar nuevas recomendaciones usando Groq AI
        log.info("Generating NEW recommendations using Groq AI for user {}", userId);

        // 1. Obtener resultados de los tests
        Map<String, Object> personalityResults = getTestResults(userId, "personality");
        Map<String, Object> vocationalResults = getTestResults(userId, "vocational_interests");
        Map<String, Object> cognitiveResults = getTestResults(userId, "cognitive_skills");

        // Validar que el usuario haya completado al menos un test
        if (personalityResults.isEmpty() && vocationalResults.isEmpty() && cognitiveResults.isEmpty()) {
            throw new IllegalStateException("User must complete at least one evaluation to get recommendations");
        }

        // 2. Obtener todas las carreras disponibles
        List<Career> allCareers = careerRepository.findAll();
        if (allCareers.isEmpty()) {
            throw new IllegalStateException("No careers available in the system");
        }

        // 3. Preparar información de carreras para Groq AI
        List<GroqService.CareerInfo> careerInfoList = allCareers.stream()
                .map(c -> new GroqService.CareerInfo(
                        c.getId(),
                        c.getName(),
                        c.getDescription(),
                        c.getDurationSemesters(),
                        c.getAverageSalary()
                ))
                .collect(Collectors.toList());

        // 4. Llamar a Groq AI para generar recomendaciones
        List<GroqService.CareerRecommendation> aiRecommendations;
        try {
            aiRecommendations = groqService.generateCareerRecommendations(
                    personalityResults,
                    vocationalResults,
                    cognitiveResults,
                    careerInfoList
            );
        } catch (Exception e) {
            log.error("Error generating recommendations with Groq AI", e);
            throw new RuntimeException("Failed to generate career recommendations", e);
        }

        // 5. Guardar recomendaciones en la base de datos
        List<CareerRecommendation> savedRecommendations = new ArrayList<>();
        for (GroqService.CareerRecommendation aiRec : aiRecommendations) {
            Career career = careerRepository.findById(aiRec.getCareerId())
                    .orElseThrow(() -> new EntityNotFoundException("Career not found: " + aiRec.getCareerId()));

            CareerRecommendation recommendation = new CareerRecommendation();
            recommendation.setUser(user);
            recommendation.setCareer(career);
            recommendation.setCompatibilityPercentage(BigDecimal.valueOf(aiRec.getCompatibilityPercentage()));

            savedRecommendations.add(careerRecommendationRepository.save(recommendation));
        }

        // 6. Construir respuesta
        List<CareerRecommendationResponse> responses = savedRecommendations.stream()
                .map(CareerMapper::toRecommendationResponse)
                .collect(Collectors.toList());

        // 7. Guardar en caché
        cache.put(userId, new CachedRecommendations(responses));

        log.info("Successfully generated and saved {} recommendations for user {}", responses.size(), userId);
        return responses;
    }

    private Map<String, Object> getTestResults(Integer userId, String testTypeName) {
        // Buscar evaluación más reciente del usuario para este tipo de test
        List<CompletedEvaluation> evaluations = completedEvaluationRepository.findByUserIdOrderByCompletionDateDesc(userId);

        Optional<CompletedEvaluation> relevantEvaluation = evaluations.stream()
                .filter(e -> testTypeName.equals(e.getTest().getTestType().getName()))
                .findFirst();

        if (relevantEvaluation.isEmpty()) {
            log.debug("User {} has not completed {} test", userId, testTypeName);
            return Collections.emptyMap();
        }

        // Obtener resultado JSON
        CompletedEvaluation evaluation = relevantEvaluation.get();
        if (evaluation.getEvaluationResult() == null) {
            log.warn("No result found for evaluation {}", evaluation.getId());
            return Collections.emptyMap();
        }

        try {
            String jsonResult = evaluation.getEvaluationResult().getResultJson();
            return objectMapper.readValue(jsonResult, Map.class);
        } catch (Exception e) {
            log.error("Error parsing {} test results for user {}", testTypeName, userId, e);
            return Collections.emptyMap();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CareerDetailResponse getCareerDetails(Integer careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("Career not found"));

        // Obtener información adicional de API de medios sociales
        Object socialMediaData = socialMediaApiService.getCareerInformation(career.getName());

        return CareerMapper.toDetailResponse(career, socialMediaData);
    }

    // Clase interna para caché
    private static class CachedRecommendations {
        private final List<CareerRecommendationResponse> recommendations;
        private final LocalDateTime expiresAt;

        public CachedRecommendations(List<CareerRecommendationResponse> recommendations) {
            this.recommendations = recommendations;
            this.expiresAt = LocalDateTime.now().plusHours(1); // Caché de 1 hora
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }

        public List<CareerRecommendationResponse> getRecommendations() {
            return recommendations;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareerResponse> getAllCareers(int page, int pageSize) {
        log.info("Fetching careers with pagination: page={}, pageSize={}", page, pageSize);

        // Validar parámetros de paginación
        if (page < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page must be >= 0 and pageSize must be > 0");
        }

        // Crear objeto Pageable con ordenamiento por nombre
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("name").ascending());

        // Obtener página de carreras
        Page<Career> careerPage = careerRepository.findAll(pageable);

        // Verificar si hay carreras
        if (careerPage.isEmpty() && page == 0) {
            throw new IllegalArgumentException("No hay carreras disponibles en el sistema");
        }

        // Convertir a DTO
        List<CareerResponse> careers = careerPage.getContent().stream()
                .map(AdminMapper::toCareerResponse)
                .collect(Collectors.toList());

        log.info("Returned {} careers out of {} total (page {} of {})",
                careers.size(),
                careerPage.getTotalElements(),
                page + 1,
                careerPage.getTotalPages());

        return careers;
    }

    @Override
    @Transactional(readOnly = true)
    public CareerResponse getCareerById(Integer careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("The requested career has not been found"));

        return AdminMapper.toCareerResponse(career);
    }

    @Override
    @Transactional
    public CareerResponse createCareer(CareerRequest request) {
        Career career = AdminMapper.toCareerEntity(request);
        Career saved = careerRepository.save(career);
        return AdminMapper.toCareerResponse(saved);
    }

    @Override
    @Transactional
    public CareerResponse updateCareer(Integer careerId, CareerRequest request) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("The career to update has not been found"));

        AdminMapper.copyToCareerEntity(request, career);
        Career saved = careerRepository.save(career);
        return AdminMapper.toCareerResponse(saved);
    }
}