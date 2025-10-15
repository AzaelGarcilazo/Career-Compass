package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.FavoriteSpecializationRequest;
import compass.career.CareerCompass.dto.FavoriteSpecializationResponse;
import compass.career.CareerCompass.dto.SpecializationDetailResponse;
import compass.career.CareerCompass.dto.SpecializationRecommendationResponse;

import java.util.List;

public interface SpecializationService {
    List<SpecializationRecommendationResponse> getRecommendedSpecializations(Integer userId);
    SpecializationDetailResponse getSpecializationDetails(Integer specializationId);
    FavoriteSpecializationResponse addFavoriteSpecialization(Integer userId, FavoriteSpecializationRequest request);
    void removeFavoriteSpecialization(Integer userId, Integer specializationId);
    List<FavoriteSpecializationResponse> getFavoriteSpecializations(Integer userId, int page, int pageSize);
}
