package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.EvaluationHistoryResponse;
import compass.career.CareerCompass.dto.EvaluationResultResponse;
import compass.career.CareerCompass.dto.SubmitTestRequest;
import compass.career.CareerCompass.dto.TestResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final AuthService authService;

    @GetMapping("/personality-test")
    public TestResponse getPersonalityTest() {
        return evaluationService.getPersonalityTest();
    }

    @PostMapping("/personality-test")
    public ResponseEntity<EvaluationResultResponse> submitPersonalityTest(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody SubmitTestRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        EvaluationResultResponse response = evaluationService.submitPersonalityTest(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/evaluations/history"))
                .body(response);
    }

    @GetMapping("/vocational-interests-test")
    public TestResponse getVocationalInterestsTest() {
        return evaluationService.getVocationalInterestsTest();
    }

    @PostMapping("/vocational-interests-test")
    public ResponseEntity<EvaluationResultResponse> submitVocationalInterestsTest(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody SubmitTestRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        EvaluationResultResponse response = evaluationService.submitVocationalInterestsTest(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/evaluations/history"))
                .body(response);
    }

    @GetMapping("/cognitive-skills-test")
    public TestResponse getCognitiveSkillsTest() {
        return evaluationService.getCognitiveSkillsTest();
    }

    @PostMapping("/cognitive-skills-test")
    public ResponseEntity<EvaluationResultResponse> submitCognitiveSkillsTest(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody SubmitTestRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        EvaluationResultResponse response = evaluationService.submitCognitiveSkillsTest(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/evaluations/history"))
                .body(response);
    }

    @GetMapping("/history")
    public List<EvaluationHistoryResponse> getEvaluationHistory(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return evaluationService.getEvaluationHistory(user.getId());
    }
}
