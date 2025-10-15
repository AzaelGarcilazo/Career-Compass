package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.CareerDetailResponse;
import compass.career.CareerCompass.dto.CareerRecommendationResponse;
import compass.career.CareerCompass.dto.FavoriteCareerRequest;
import compass.career.CareerCompass.dto.FavoriteCareerResponse;

import java.util.List;

public interface CareerService {
    List<CareerRecommendationResponse> getRecommendedCareers(Integer userId);
    CareerDetailResponse getCareerDetails(Integer careerId);
    FavoriteCareerResponse addFavoriteCareer(Integer userId, FavoriteCareerRequest request);
    void removeFavoriteCareer(Integer userId, Integer careerId);
    List<FavoriteCareerResponse> getFavoriteCareers(Integer userId, int page, int pageSize);
}
