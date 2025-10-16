package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.*;

import java.util.List;

public interface CareerService {
    List<CareerRecommendationResponse> getRecommendedCareers(Integer userId);
    CareerDetailResponse getCareerDetails(Integer careerId);

    List<CareerResponse> getAllCareers();
    CareerResponse getCareerById(Integer careerId);
    CareerResponse createCareer(CareerRequest request);
    CareerResponse updateCareer(Integer careerId, CareerRequest request);
}
