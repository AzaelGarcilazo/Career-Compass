package compass.career.careercompass.service;

import compass.career.careercompass.dto.FavoriteSpecializationRequest;
import compass.career.careercompass.dto.FavoriteSpecializationResponse;

import java.util.List;

public interface FavoriteSpecializationService {
    FavoriteSpecializationResponse addFavoriteSpecialization(Integer userId, FavoriteSpecializationRequest request);
    void removeFavoriteSpecialization(Integer userId, Integer specializationId);
    List<FavoriteSpecializationResponse> getFavoriteSpecializations(Integer userId, int page, int pageSize);
}
