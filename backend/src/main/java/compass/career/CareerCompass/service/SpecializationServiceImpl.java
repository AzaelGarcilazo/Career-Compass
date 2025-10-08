package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.FavoriteSpecializationRequest;
import compass.career.CareerCompass.dto.FavoriteSpecializationResponse;
import compass.career.CareerCompass.dto.SpecializationDetailResponse;
import compass.career.CareerCompass.dto.SpecializationRecommendationResponse;
import compass.career.CareerCompass.mapper.SpecializationMapper;
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
public class SpecializationServiceImpl implements SpecializationService {

    private final SpecializationAreaRepository specializationAreaRepository;
    private final SpecializationRecommendationRepository specializationRecommendationRepository;
    private final FavoriteSpecializationRepository favoriteSpecializationRepository;
    private final UserRepository userRepository;
    private final CompletedEvaluationRepository completedEvaluationRepository;
    private final SkillRepository skillRepository;
    private final SocialMediaApiService socialMediaApiService;

    @Override
    @Transactional
    public List<SpecializationRecommendationResponse> getRecommendedSpecializations(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Verificar si ya existen recomendaciones
        List<SpecializationRecommendation> existingRecommendations =
                specializationRecommendationRepository.findByUserIdOrderByCompatibilityPercentageDesc(userId);

        if (!existingRecommendations.isEmpty()) {
            return existingRecommendations.stream()
                    .map(SpecializationMapper::toRecommendationResponse)
                    .collect(Collectors.toList());
        }

        // Generar nuevas recomendaciones basadas en evaluaciones y h
// Generar nuevas recomendaciones basadas en evaluaciones y habilidades
        List<CompletedEvaluation> evaluations =
                completedEvaluationRepository.findByUserIdOrderByCompletionDateDesc(userId);

        if (evaluations.isEmpty()) {
            throw new IllegalStateException("User must complete at least one evaluation to get recommendations");
        }

        // Obtener habilidades actuales del usuario
        List<Skill> userSkills = skillRepository.findByUserId(userId);

        // Obtener todas las áreas de especialización
        List<SpecializationArea> allSpecializations = specializationAreaRepository.findAll();
        List<SpecializationRecommendation> recommendations = new ArrayList<>();

        for (SpecializationArea specialization : allSpecializations) {
            // Calcular compatibilidad basada en intereses, habilidades y tendencias
            BigDecimal compatibility = calculateSpecializationCompatibility(
                    specialization, userSkills, evaluations);

            SpecializationRecommendation recommendation = new SpecializationRecommendation();
            recommendation.setUser(user);
            recommendation.setSpecializationArea(specialization);
            recommendation.setCompatibilityPercentage(compatibility);
            recommendations.add(recommendation);
        }

        // Ordenar por compatibilidad y guardar
        recommendations.sort(Comparator.comparing(SpecializationRecommendation::getCompatibilityPercentage).reversed());

        specializationRecommendationRepository.saveAll(recommendations);

        return recommendations.stream()
                .map(SpecializationMapper::toRecommendationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecializationDetailResponse getSpecializationDetails(Integer specializationId) {
        SpecializationArea specialization = specializationAreaRepository.findById(specializationId)
                .orElseThrow(() -> new EntityNotFoundException("Specialization area not found"));

        // Obtener información adicional de API de medios sociales
        Object socialMediaData = socialMediaApiService.getSpecializationInformation(specialization.getName());

        return SpecializationMapper.toDetailResponse(specialization, socialMediaData);
    }

    @Override
    @Transactional
    public FavoriteSpecializationResponse addFavoriteSpecialization(Integer userId, FavoriteSpecializationRequest request) {
        // Validar máximo 5 favoritas
        if (favoriteSpecializationRepository.countByUserIdAndActiveTrue(userId) >= 5) {
            throw new IllegalArgumentException("Maximum 5 favorite specializations allowed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        SpecializationArea specialization = specializationAreaRepository.findById(request.getSpecializationAreaId())
                .orElseThrow(() -> new EntityNotFoundException("Specialization area not found"));

        // Verificar si ya existe como favorita
        Optional<FavoriteSpecialization> existing = favoriteSpecializationRepository
                .findByUserIdAndSpecializationAreaId(userId, request.getSpecializationAreaId());

        if (existing.isPresent()) {
            FavoriteSpecialization favorite = existing.get();
            if (favorite.getActive()) {
                throw new IllegalArgumentException("Specialization already in favorites");
            }
            // Reactivar favorita
            favorite.setActive(true);
            favorite.setNotes(request.getNotes());
            FavoriteSpecialization saved = favoriteSpecializationRepository.save(favorite);
            return SpecializationMapper.toFavoriteResponse(saved);
        }

        FavoriteSpecialization favorite = SpecializationMapper.toFavoriteEntity(request, user, specialization);
        FavoriteSpecialization saved = favoriteSpecializationRepository.save(favorite);
        return SpecializationMapper.toFavoriteResponse(saved);
    }

    @Override
    @Transactional
    public void removeFavoriteSpecialization(Integer userId, Integer specializationId) {
        FavoriteSpecialization favorite = favoriteSpecializationRepository
                .findByUserIdAndSpecializationAreaId(userId, specializationId)
                .orElseThrow(() -> new EntityNotFoundException("Favorite specialization not found"));

        favorite.setActive(false);
        favoriteSpecializationRepository.save(favorite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteSpecializationResponse> getFavoriteSpecializations(Integer userId) {
        return favoriteSpecializationRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(SpecializationMapper::toFavoriteResponse)
                .collect(Collectors.toList());
    }

    // Método auxiliar para calcular compatibilidad
    private BigDecimal calculateSpecializationCompatibility(SpecializationArea specialization,
                                                            List<Skill> userSkills,
                                                            List<CompletedEvaluation> evaluations) {
        // Algoritmo basado en habilidades actuales y tendencias del mercado

        BigDecimal baseCompatibility = BigDecimal.valueOf(50); // Base 50%

        // Ajustar según habilidades relevantes (máximo +30%)
        BigDecimal skillBonus = BigDecimal.ZERO;
        for (Skill skill : userSkills) {
            if (specialization.getDescription() != null &&
                    specialization.getDescription().toLowerCase().contains(skill.getSkillName().toLowerCase())) {
                // Bonus proporcional al nivel de la habilidad
                skillBonus = skillBonus.add(BigDecimal.valueOf(skill.getProficiencyLevel() * 6.0));
            }
        }
        if (skillBonus.compareTo(BigDecimal.valueOf(30)) > 0) {
            skillBonus = BigDecimal.valueOf(30);
        }

        // Ajustar según evaluaciones cognitivas (máximo +20%)
        BigDecimal cognitiveBonus = BigDecimal.ZERO;
        for (CompletedEvaluation eval : evaluations) {
            if ("cognitive_skills".equals(eval.getTest().getTestType().getName())) {
                if (eval.getTotalScore() != null) {
                    cognitiveBonus = eval.getTotalScore().multiply(BigDecimal.valueOf(0.2));
                }
                break;
            }
        }

        BigDecimal total = baseCompatibility.add(skillBonus).add(cognitiveBonus);

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