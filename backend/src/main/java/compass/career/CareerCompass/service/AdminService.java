package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.*;

import java.util.List;

public interface AdminService {
    List<TestListResponse> getAllTests();
    TestResponse getTestDetails(Integer testId);
    TestResponse createTest(CreateTestRequest request);
    TestResponse updateTest(Integer testId, CreateTestRequest request);

    List<CareerResponse> getAllCareers();
    CareerResponse getCareerDetails(Integer careerId);
    CareerResponse createCareer(CareerRequest request);
    CareerResponse updateCareer(Integer careerId, CareerRequest request);

    List<SpecializationAreaResponse> getAllSpecializations();
    SpecializationAreaResponse getSpecializationDetails(Integer specializationId);
    SpecializationAreaResponse createSpecialization(SpecializationAreaRequest request);
    SpecializationAreaResponse updateSpecialization(Integer specializationId, SpecializationAreaRequest request);
}
