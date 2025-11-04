package compass.career.careercompass.service;

import compass.career.careercompass.dto.CreateTestRequest;
import compass.career.careercompass.dto.TestListResponse;
import compass.career.careercompass.dto.TestResponse;

import java.util.List;

public interface TestService {
    List<TestListResponse> getAllTests();
    TestResponse getTestDetails(Integer testId);
    TestResponse createTest(CreateTestRequest request);
    TestResponse updateTest(Integer testId, CreateTestRequest request);
}