package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.CreateTestRequest;
import compass.career.CareerCompass.dto.TestListResponse;
import compass.career.CareerCompass.dto.TestResponse;

import java.util.List;

public interface TestService {
    List<TestListResponse> getAllTests();
    TestResponse getTestDetails(Integer testId);
    TestResponse createTest(CreateTestRequest request);
    TestResponse updateTest(Integer testId, CreateTestRequest request);
}