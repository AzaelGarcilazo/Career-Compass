package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.*;

import java.util.List;

public interface CareerService {
    List<CareerRecommendationResponse> getRecommendedCareers(Integer userId);
    CareerDetailResponse getCareerDetails(Integer careerId);

    // Método con paginación
    List<CareerResponse> getAllCareers(int page, int pageSize);

    CareerResponse getCareerById(Integer careerId);
    CareerResponse createCareer(CareerRequest request);
    CareerResponse updateCareer(Integer careerId, CareerRequest request);
}