package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.*;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Evaluations", description = "Endpoints for vocational assessments and psychometric tests")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final AuthService authService;

    @GetMapping("/personality-test")
    @Operation(
            summary = "Get personality test",
            description = "Retrieves the questions for the personality test based on the Holland RIASEC model. Questions are randomly selected from the active question bank to ensure variety in each application."
    )
    public TestResponse getPersonalityTest() {
        return evaluationService.getPersonalityTest();
    }

    @PostMapping("/personality-test")
    @Operation(
            summary = "Submit personality test answers",
            description = "Processes the user's responses to the personality test and generates an analysis using Azure Cognitive Services. Calculates the 5 personality dimensions (conscientiousness, openness, neuroticism, extraversion, agreeableness), performs sentiment analysis, and extracts key phrases. Results are stored in JSON format for later use in generating recommendations."
    )
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
    @Operation(
            summary = "Get vocational interests test",
            description = "Retrieves the questions for the vocational interests test. This test evaluates the user's areas of professional interest and their affinities with different work fields. Questions are randomly selected for each application."
    )
    public TestResponse getVocationalInterestsTest() {
        return evaluationService.getVocationalInterestsTest();
    }

    @PostMapping("/vocational-interests-test")
    @Operation(
            summary = "Submit vocational interests test answers",
            description = "Processes the vocational interests test responses and calculates affinity percentages with different professional areas. Identifies the user's top 5 vocational areas and stores them in the area_results table with their corresponding ranking. These results are fundamental for generating personalized career recommendations."
    )
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
    @Operation(
            summary = "Get cognitive skills test",
            description = "Retrieves the questions for the cognitive skills test. This test evaluates mental abilities such as logical reasoning, memory, attention, verbal comprehension, and numerical skills. Questions are randomly selected from the active bank."
    )
    public TestResponse getCognitiveSkillsTest() {
        return evaluationService.getCognitiveSkillsTest();
    }

    @PostMapping("/cognitive-skills-test")
    @Operation(
            summary = "Submit cognitive skills test answers",
            description = "Processes the cognitive skills test responses and calculates scores from 0-100 for each evaluated area (logical reasoning, memory, etc.). Determines the proficiency level (low, medium, high) for each skill and generates an overall average score. Results help identify the user's strengths and areas for improvement."
    )
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
    @Operation(
            summary = "Get evaluation history",
            description = "Retrieves the complete history of evaluations completed by the user, ordered from most recent to oldest. Includes information for all types of completed tests (personality, vocational interests, and cognitive skills) with their completion dates and obtained scores."
    )
    public List<EvaluationHistoryResponse> getEvaluationHistory(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return evaluationService.getEvaluationHistory(user.getId());
    }

    @GetMapping("/details/{evaluationId}")
    @Operation(
            summary = "Get evaluation detail",
            description = "Retrieves the complete details of a specific evaluation, including all questions answered by the user, the selected options, and the analysis results. This allows users to review their past test responses and see how they answered each question."
    )
    public ResponseEntity<EvaluationDetailResponse> getEvaluationDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer evaluationId) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        EvaluationDetailResponse response = evaluationService.getEvaluationDetail(user.getId(), evaluationId);
        return ResponseEntity.ok(response);
    }
}