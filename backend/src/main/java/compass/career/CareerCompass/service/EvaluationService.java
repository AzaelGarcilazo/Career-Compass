package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.*;

import java.util.List;

public interface EvaluationService {
    TestResponse getPersonalityTest();
    EvaluationResultResponse submitPersonalityTest(Integer userId, SubmitTestRequest request);
    TestResponse getVocationalInterestsTest();
    EvaluationResultResponse submitVocationalInterestsTest(Integer userId, SubmitTestRequest request);
    TestResponse getCognitiveSkillsTest();
    EvaluationResultResponse submitCognitiveSkillsTest(Integer userId, SubmitTestRequest request);
    List<EvaluationHistoryResponse> getEvaluationHistory(Integer userId);

    EvaluationDetailResponse getEvaluationDetail(Integer userId, Integer evaluationId);
}
