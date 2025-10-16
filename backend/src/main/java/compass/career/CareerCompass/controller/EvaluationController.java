package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.EvaluationHistoryResponse;
import compass.career.CareerCompass.dto.EvaluationResultResponse;
import compass.career.CareerCompass.dto.SubmitTestRequest;
import compass.career.CareerCompass.dto.TestResponse;
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
@Tag(name = "Evaluations", description = "Endpoints para evaluaciones vocacionales y tests psicométricos")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final AuthService authService;

    @GetMapping("/personality-test")
    @Operation(
            summary = "Obtener test de personalidad",
            description = "Recupera las preguntas del test de personalidad basado en el modelo Holland RIASEC. Las preguntas se seleccionan aleatoriamente del banco de preguntas activas del test para garantizar variedad en cada aplicación."
    )
    public TestResponse getPersonalityTest() {
        return evaluationService.getPersonalityTest();
    }

    @PostMapping("/personality-test")
    @Operation(
            summary = "Enviar respuestas del test de personalidad",
            description = "Procesa las respuestas del usuario al test de personalidad y genera un análisis utilizando Azure Cognitive Services. Calcula las 5 dimensiones de personalidad (conscientiousness, openness, neuroticism, extraversion, agreeableness), realiza análisis de sentimientos y extrae frases clave. Los resultados se almacenan en formato JSON para su posterior uso en la generación de recomendaciones."
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
            summary = "Obtener test de intereses vocacionales",
            description = "Recupera las preguntas del test de intereses vocacionales. Este test evalúa las áreas de interés profesional del usuario y sus afinidades con diferentes campos laborales. Las preguntas se seleccionan aleatoriamente para cada aplicación."
    )
    public TestResponse getVocationalInterestsTest() {
        return evaluationService.getVocationalInterestsTest();
    }

    @PostMapping("/vocational-interests-test")
    @Operation(
            summary = "Enviar respuestas del test de intereses vocacionales",
            description = "Procesa las respuestas del test de intereses vocacionales y calcula los porcentajes de afinidad con diferentes áreas profesionales. Identifica las top 5 áreas vocacionales del usuario y las almacena en la tabla area_results con su ranking correspondiente. Estos resultados son fundamentales para generar recomendaciones de carreras personalizadas."
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
            summary = "Obtener test de habilidades cognitivas",
            description = "Recupera las preguntas del test de habilidades cognitivas. Este test evalúa capacidades mentales como razonamiento lógico, memoria, atención, comprensión verbal y habilidades numéricas. Las preguntas son seleccionadas aleatoriamente del banco activo."
    )
    public TestResponse getCognitiveSkillsTest() {
        return evaluationService.getCognitiveSkillsTest();
    }

    @PostMapping("/cognitive-skills-test")
    @Operation(
            summary = "Enviar respuestas del test de habilidades cognitivas",
            description = "Procesa las respuestas del test de habilidades cognitivas y calcula puntuaciones de 0-100 para cada área evaluada (razonamiento lógico, memoria, etc.). Determina el nivel de dominio (bajo, medio, alto) de cada habilidad y genera un score total promedio. Los resultados ayudan a identificar fortalezas y áreas de mejora del usuario."
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
            summary = "Obtener historial de evaluaciones",
            description = "Recupera el historial completo de evaluaciones realizadas por el usuario, ordenadas de más reciente a más antigua. Incluye información de todos los tipos de tests completados (personalidad, intereses vocacionales y habilidades cognitivas) con sus fechas de realización y puntuaciones obtenidas."
    )
    public List<EvaluationHistoryResponse> getEvaluationHistory(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return evaluationService.getEvaluationHistory(user.getId());
    }
}