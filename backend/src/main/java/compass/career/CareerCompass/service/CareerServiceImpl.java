package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.CareerDetailResponse;
import compass.career.CareerCompass.dto.CareerRecommendationResponse;
import compass.career.CareerCompass.dto.FavoriteCareerRequest;
import compass.career.CareerCompass.dto.FavoriteCareerResponse;
import compass.career.CareerCompass.mapper.CareerMapper;
import compass.career.CareerCompass.model.*;
import compass.career.CareerCompass.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService {

    private final CareerRepository careerRepository;
    private final CareerRecommendationRepository careerRecommendationRepository;
    private final FavoriteCareerRepository favoriteCareerRepository;
    private final UserRepository userRepository;
    private final CompletedEvaluationRepository completedEvaluationRepository;
    private final AreaResultRepository areaResultRepository;
    private final SocialMediaApiService socialMediaApiService;

    @Override
    @Transactional
    public List<CareerRecommendationResponse> getRecommendedCareers(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Verificar si ya existen recomendaciones
        List<CareerRecommendation> existingRecommendations =
                careerRecommendationRepository.findByUserIdOrderByCompatibilityPercentageDesc(userId);

        if (!existingRecommendations.isEmpty()) {
            return existingRecommendations.stream()
                    .map(CareerMapper::toRecommendationResponse)
                    .collect(Collectors.toList());
        }

        // Generar nuevas recomendaciones basadas en evaluaciones
        List<CompletedEvaluation> evaluations =
                completedEvaluationRepository.findByUserIdOrderByCompletionDateDesc(userId);

        if (evaluations.isEmpty()) {
            throw new IllegalStateException("User must complete at least one evaluation to get recommendations");
        }

        // Obtener áreas vocacionales del usuario
        Map<String, BigDecimal> userInterests = new HashMap<>();
        for (CompletedEvaluation evaluation : evaluations) {
            if ("vocational_interests".equals(evaluation.getTest().getTestType().getName())) {
                List<AreaResult> areaResults = areaResultRepository.findByEvaluationIdOrderByRankingAsc(evaluation.getId());
                for (AreaResult areaResult : areaResults) {
                    String areaName = areaResult.getVocationalArea().getName();
                    BigDecimal percentage = areaResult.getPercentage();
                    userInterests.put(areaName, percentage);
                }
                break; // Tomar solo la evaluación más reciente
            }
        }

        // Obtener todas las carreras y calcular compatibilidad
        List<Career> allCareers = careerRepository.findAll();
        List<CareerRecommendation> recommendations = new ArrayList<>();

        for (Career career : allCareers) {
            // Algoritmo simple de compatibilidad basado en áreas de interés
            BigDecimal compatibility = calculateCareerCompatibility(career, userInterests, evaluations);

            CareerRecommendation recommendation = new CareerRecommendation();
            recommendation.setUser(user);
            recommendation.setCareer(career);
            recommendation.setCompatibilityPercentage(compatibility);
            recommendations.add(recommendation);
        }

        // Ordenar por compatibilidad y tomar mínimo 10
        recommendations.sort(Comparator.comparing(CareerRecommendation::getCompatibilityPercentage).reversed());

        // Guardar las recomendaciones
        List<CareerRecommendation> topRecommendations = recommendations.stream()
                .limit(Math.max(10, recommendations.size()))
                .collect(Collectors.toList());

        careerRecommendationRepository.saveAll(topRecommendations);

        return topRecommendations.stream()
                .map(CareerMapper::toRecommendationResponse)
                .collect(Collectors.toList());
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

    @Override
    @Transactional
    public FavoriteCareerResponse addFavoriteCareer(Integer userId, FavoriteCareerRequest request) {
        // Validar máximo 10 favoritas
        if (favoriteCareerRepository.countByUserIdAndActiveTrue(userId) >= 10) {
            throw new IllegalArgumentException("Maximum 10 favorite careers allowed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Career career = careerRepository.findById(request.getCareerId())
                .orElseThrow(() -> new EntityNotFoundException("Career not found"));

        // Verificar si ya existe como favorita
        Optional<FavoriteCareer> existing = favoriteCareerRepository.findByUserIdAndCareerId(userId, request.getCareerId());

        if (existing.isPresent()) {
            FavoriteCareer favorite = existing.get();
            if (favorite.getActive()) {
                throw new IllegalArgumentException("Career already in favorites");
            }
            // Reactivar favorita
            favorite.setActive(true);
            favorite.setNotes(request.getNotes());
            FavoriteCareer saved = favoriteCareerRepository.save(favorite);
            return CareerMapper.toFavoriteResponse(saved);
        }

        FavoriteCareer favorite = CareerMapper.toFavoriteEntity(request, user, career);
        FavoriteCareer saved = favoriteCareerRepository.save(favorite);
        return CareerMapper.toFavoriteResponse(saved);
    }

    @Override
    @Transactional
    public void removeFavoriteCareer(Integer userId, Integer careerId) {
        FavoriteCareer favorite = favoriteCareerRepository.findByUserIdAndCareerId(userId, careerId)
                .orElseThrow(() -> new EntityNotFoundException("Favorite career not found"));

        favorite.setActive(false);
        favoriteCareerRepository.save(favorite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteCareerResponse> getFavoriteCareers(Integer userId) {
        return favoriteCareerRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(CareerMapper::toFavoriteResponse)
                .collect(Collectors.toList());
    }

    // Método auxiliar para calcular compatibilidad
    private BigDecimal calculateCareerCompatibility(Career career, Map<String, BigDecimal> userInterests,
                                                    List<CompletedEvaluation> evaluations) {
        // Algoritmo simple: basado en coincidencia de palabras clave y áreas de interés
        // En producción, esto podría usar IA/ML más sofisticado

        BigDecimal baseCompatibility = BigDecimal.valueOf(50); // Base 50%

        // Ajustar según áreas de interés (máximo +30%)
        BigDecimal interestBonus = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : userInterests.entrySet()) {
            if (career.getDescription() != null &&
                    career.getDescription().toLowerCase().contains(entry.getKey().toLowerCase())) {
                interestBonus = interestBonus.add(entry.getValue().multiply(BigDecimal.valueOf(0.3)));
            }
        }

        // Ajustar según evaluación de personalidad (máximo +20%)
        BigDecimal personalityBonus = BigDecimal.ZERO;
        for (CompletedEvaluation eval : evaluations) {
            if ("personality".equals(eval.getTest().getTestType().getName())) {
                if (eval.getTotalScore() != null) {
                    personalityBonus = eval.getTotalScore().multiply(BigDecimal.valueOf(0.2));
                }
                break;
            }
        }

        BigDecimal total = baseCompatibility.add(interestBonus).add(personalityBonus);

        // Asegurar que esté entre 0 y 100
        if (total.compareTo(BigDecimal.valueOf(100)) > 0) {
            total = BigDecimal.valueOf(100);
        }
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        return total.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
