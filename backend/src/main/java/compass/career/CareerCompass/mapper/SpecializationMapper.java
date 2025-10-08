package compass.career.CareerCompass.mapper;

import compass.career.CareerCompass.dto.FavoriteSpecializationRequest;
import compass.career.CareerCompass.dto.FavoriteSpecializationResponse;
import compass.career.CareerCompass.dto.SpecializationDetailResponse;
import compass.career.CareerCompass.dto.SpecializationRecommendationResponse;
import compass.career.CareerCompass.model.FavoriteSpecialization;
import compass.career.CareerCompass.model.SpecializationArea;
import compass.career.CareerCompass.model.SpecializationRecommendation;
import compass.career.CareerCompass.model.User;

public final class SpecializationMapper {

    public static SpecializationRecommendationResponse toRecommendationResponse(SpecializationRecommendation entity) {
        if (entity == null)
            return null;

        SpecializationArea area = entity.getSpecializationArea();
        return SpecializationRecommendationResponse.builder()
                .id(area.getId())
                .name(area.getName())
                .description(area.getDescription())
                .compatibilityPercentage(entity.getCompatibilityPercentage())
                .careerName(area.getCareer().getName())
                .build();
    }

    public static SpecializationDetailResponse toDetailResponse(SpecializationArea entity, Object socialMediaData) {
        if (entity == null)
            return null;

        return SpecializationDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .applicationFields(entity.getApplicationFields())
                .jobProjection(entity.getJobProjection())
                .careerName(entity.getCareer().getName())
                .socialMediaData(socialMediaData)
                .build();
    }

    public static FavoriteSpecializationResponse toFavoriteResponse(FavoriteSpecialization entity) {
        if (entity == null)
            return null;

        return FavoriteSpecializationResponse.builder()
                .id(entity.getId())
                .specializationAreaId(entity.getSpecializationArea().getId())
                .specializationName(entity.getSpecializationArea().getName())
                .notes(entity.getNotes())
                .active(entity.getActive())
                .build();
    }

    public static FavoriteSpecialization toFavoriteEntity(
            FavoriteSpecializationRequest dto,
            User user,
            SpecializationArea area) {
        if (dto == null || user == null || area == null)
            return null;

        FavoriteSpecialization entity = new FavoriteSpecialization();
        entity.setUser(user);
        entity.setSpecializationArea(area);
        entity.setNotes(dto.getNotes());
        entity.setActive(true);

        return entity;
    }
}
