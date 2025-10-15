package compass.career.CareerCompass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import compass.career.CareerCompass.dto.*;
import compass.career.CareerCompass.mapper.EvaluationMapper;
import compass.career.CareerCompass.mapper.TestMapper;
import compass.career.CareerCompass.model.*;
import compass.career.CareerCompass.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final UserRepository userRepository;
    private final CompletedEvaluationRepository completedEvaluationRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final EvaluationResultRepository evaluationResultRepository;
    private final AreaResultRepository areaResultRepository;
    private final VocationalAreaRepository vocationalAreaRepository;
    private final AzureCognitiveService azureCognitiveService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public TestResponse getPersonalityTest() {
        Test test = testRepository.findByTestTypeNameAndActiveTrue("personality")
                .orElseThrow(() -> new EntityNotFoundException("Personality test not found"));

        // Obtener preguntas aleatorias
        List<Question> randomQuestions = questionRepository
                .findRandomActiveQuestionsByTestId(test.getId(), test.getQuestionsToShow());

        Test testWithRandomQuestions = new Test();
        testWithRandomQuestions.setId(test.getId());
        testWithRandomQuestions.setName(test.getName());
        testWithRandomQuestions.setDescription(test.getDescription());
        testWithRandomQuestions.setTestType(test.getTestType());
        testWithRandomQuestions.setQuestionsToShow(test.getQuestionsToShow());
        testWithRandomQuestions.setQuestions(randomQuestions);

        return TestMapper.toResponse(testWithRandomQuestions);
    }

    @Override
    @Transactional
    public EvaluationResultResponse submitPersonalityTest(Integer userId, SubmitTestRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        if (!"personality".equals(test.getTestType().getName())) {
            throw new IllegalArgumentException("Invalid test type");
        }

        // Validar que todas las preguntas fueron respondidas
        if (request.getAnswers().size() != test.getQuestionsToShow()) {
            throw new IllegalArgumentException("All questions must be answered");
        }

        // Crear evaluación completada
        CompletedEvaluation evaluation = new CompletedEvaluation();
        evaluation.setUser(user);
        evaluation.setTest(test);
        evaluation.setCompletionDate(LocalDateTime.now());
        evaluation = completedEvaluationRepository.save(evaluation);

        // Guardar respuestas del usuario
        Map<String, Object> responses = new HashMap<>();
        for (UserAnswerRequest answerReq : request.getAnswers()) {
            Question question = questionRepository.findById(answerReq.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question not found"));

            AnswerOption option = answerOptionRepository.findById(answerReq.getOptionId())
                    .orElseThrow(() -> new EntityNotFoundException("Answer option not found"));

            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setEvaluation(evaluation);
            userAnswer.setQuestion(question);
            userAnswer.setOption(option);
            userAnswerRepository.save(userAnswer);

            // Preparar datos para Azure
            responses.put("Q" + question.getId(), option.getOptionText());
        }

        // Llamar a Azure Cognitive Services para análisis de personalidad
        Map<String, Object> personalityAnalysis = azureCognitiveService.analyzePersonality(responses);

        // Calcular score total (promedio de dimensiones)
        BigDecimal totalScore = calculateAverageScore(personalityAnalysis);
        evaluation.setTotalScore(totalScore);
        evaluation = completedEvaluationRepository.save(evaluation);

        // Guardar resultado en JSON
        EvaluationResult result = new EvaluationResult();
        result.setEvaluation(evaluation);
        try {
            result.setResultJson(objectMapper.writeValueAsString(personalityAnalysis));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing result", e);
        }
        evaluationResultRepository.save(result);

        return EvaluationMapper.toResultResponse(evaluation);
    }

    @Override
    @Transactional(readOnly = true)
    public TestResponse getVocationalInterestsTest() {
        Test test = testRepository.findByTestTypeNameAndActiveTrue("vocational_interests")
                .orElseThrow(() -> new EntityNotFoundException("Vocational interests test not found"));

        // Obtener preguntas aleatorias
        List<Question> randomQuestions = questionRepository
                .findRandomActiveQuestionsByTestId(test.getId(), test.getQuestionsToShow());

        Test testWithRandomQuestions = new Test();
        testWithRandomQuestions.setId(test.getId());
        testWithRandomQuestions.setName(test.getName());
        testWithRandomQuestions.setDescription(test.getDescription());
        testWithRandomQuestions.setTestType(test.getTestType());
        testWithRandomQuestions.setQuestionsToShow(test.getQuestionsToShow());
        testWithRandomQuestions.setQuestions(randomQuestions);

        return TestMapper.toResponse(testWithRandomQuestions);
    }

    @Override
    @Transactional
    public EvaluationResultResponse submitVocationalInterestsTest(Integer userId, SubmitTestRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        if (!"vocational_interests".equals(test.getTestType().getName())) {
            throw new IllegalArgumentException("Invalid test type");
        }

        // Validar que todas las preguntas fueron respondidas
        if (request.getAnswers().size() != test.getQuestionsToShow()) {
            throw new IllegalArgumentException("All questions must be answered");
        }

        // Crear evaluación completada
        CompletedEvaluation evaluation = new CompletedEvaluation();
        evaluation.setUser(user);
        evaluation.setTest(test);
        evaluation.setCompletionDate(LocalDateTime.now());
        evaluation = completedEvaluationRepository.save(evaluation);

        // Guardar respuestas y calcular puntajes por área
        Map<String, Integer> areaScores = new HashMap<>();

        for (UserAnswerRequest answerReq : request.getAnswers()) {
            Question question = questionRepository.findById(answerReq.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question not found"));

            AnswerOption option = answerOptionRepository.findById(answerReq.getOptionId())
                    .orElseThrow(() -> new EntityNotFoundException("Answer option not found"));

            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setEvaluation(evaluation);
            userAnswer.setQuestion(question);
            userAnswer.setOption(option);
            userAnswerRepository.save(userAnswer);

            // Acumular puntajes por categoría/área
            if (option.getCategory() != null) {
                areaScores.put(option.getCategory(),
                        areaScores.getOrDefault(option.getCategory(), 0) +
                                (option.getWeightValue() != null ? option.getWeightValue() : 0));
            }
        }

        // Calcular porcentajes (suma debe ser 100%)
        int totalScore = areaScores.values().stream().mapToInt(Integer::intValue).sum();
        Map<String, BigDecimal> areaPercentages = new HashMap<>();

        for (Map.Entry<String, Integer> entry : areaScores.entrySet()) {
            BigDecimal percentage = totalScore > 0 ?
                    BigDecimal.valueOf(entry.getValue() * 100.0 / totalScore).setScale(2, BigDecimal.ROUND_HALF_UP) :
                    BigDecimal.ZERO;
            areaPercentages.put(entry.getKey(), percentage);
        }

        // Ordenar áreas por porcentaje (top 5)
        List<Map.Entry<String, BigDecimal>> sortedAreas = areaPercentages.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Guardar resultados por área
        int ranking = 1;
        for (Map.Entry<String, BigDecimal> entry : sortedAreas) {
            VocationalArea area = vocationalAreaRepository.findByName(entry.getKey())
                    .orElseGet(() -> {
                        VocationalArea newArea = new VocationalArea();
                        newArea.setName(entry.getKey());
                        return vocationalAreaRepository.save(newArea);
                    });

            AreaResult areaResult = new AreaResult();
            areaResult.setEvaluation(evaluation);
            areaResult.setVocationalArea(area);
            areaResult.setPercentage(entry.getValue());
            areaResult.setRanking(ranking++);
            areaResultRepository.save(areaResult);
        }

        // Calcular score promedio
        BigDecimal avgScore = sortedAreas.isEmpty() ? BigDecimal.ZERO :
                sortedAreas.stream()
                        .map(Map.Entry::getValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(sortedAreas.size()), 2, BigDecimal.ROUND_HALF_UP);

        evaluation.setTotalScore(avgScore);
        evaluation = completedEvaluationRepository.save(evaluation);

        // Preparar resultado con recomendaciones
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("topAreas", sortedAreas.stream()
                .map(e -> Map.of("area", e.getKey(), "percentage", e.getValue()))
                .collect(Collectors.toList()));
        resultData.put("recommendations", generateVocationalRecommendations(sortedAreas));

        // Guardar resultado en JSON
        EvaluationResult result = new EvaluationResult();
        result.setEvaluation(evaluation);
        try {
            result.setResultJson(objectMapper.writeValueAsString(resultData));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing result", e);
        }
        evaluationResultRepository.save(result);

        return EvaluationMapper.toResultResponse(evaluation);
    }

    @Override
    @Transactional(readOnly = true)
    public TestResponse getCognitiveSkillsTest() {
        Test test = testRepository.findByTestTypeNameAndActiveTrue("cognitive_skills")
                .orElseThrow(() -> new EntityNotFoundException("Cognitive skills test not found"));

        // Obtener preguntas aleatorias
        List<Question> randomQuestions = questionRepository
                .findRandomActiveQuestionsByTestId(test.getId(), test.getQuestionsToShow());

        Test testWithRandomQuestions = new Test();
        testWithRandomQuestions.setId(test.getId());
        testWithRandomQuestions.setName(test.getName());
        testWithRandomQuestions.setDescription(test.getDescription());
        testWithRandomQuestions.setTestType(test.getTestType());
        testWithRandomQuestions.setQuestionsToShow(test.getQuestionsToShow());
        testWithRandomQuestions.setQuestions(randomQuestions);

        return TestMapper.toResponse(testWithRandomQuestions);
    }

    @Override
    @Transactional
    public EvaluationResultResponse submitCognitiveSkillsTest(Integer userId, SubmitTestRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        if (!"cognitive_skills".equals(test.getTestType().getName())) {
            throw new IllegalArgumentException("Invalid test type");
        }

        // Validar que todas las preguntas fueron respondidas
        if (request.getAnswers().size() != test.getQuestionsToShow()) {
            throw new IllegalArgumentException("All questions must be answered");
        }

        // Crear evaluación completada
        CompletedEvaluation evaluation = new CompletedEvaluation();
        evaluation.setUser(user);
        evaluation.setTest(test);
        evaluation.setCompletionDate(LocalDateTime.now());
        evaluation = completedEvaluationRepository.save(evaluation);

        // Guardar respuestas y calcular puntajes por área cognitiva
        Map<String, Integer> cognitiveScores = new HashMap<>();
        Map<String, Integer> cognitiveMaxScores = new HashMap<>();

        for (UserAnswerRequest answerReq : request.getAnswers()) {
            Question question = questionRepository.findById(answerReq.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question not found"));

            AnswerOption option = answerOptionRepository.findById(answerReq.getOptionId())
                    .orElseThrow(() -> new EntityNotFoundException("Answer option not found"));

            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setEvaluation(evaluation);
            userAnswer.setQuestion(question);
            userAnswer.setOption(option);
            userAnswerRepository.save(userAnswer);

            // Acumular puntajes por categoría cognitiva
            if (option.getCategory() != null && option.getWeightValue() != null) {
                String category = option.getCategory();
                cognitiveScores.put(category,
                        cognitiveScores.getOrDefault(category, 0) + option.getWeightValue());

                // Encontrar el máximo peso posible para esta pregunta
                int maxWeight = question.getAnswerOptions().stream()
                        .filter(o -> category.equals(o.getCategory()))
                        .mapToInt(o -> o.getWeightValue() != null ? o.getWeightValue() : 0)
                        .max()
                        .orElse(0);

                cognitiveMaxScores.put(category,
                        cognitiveMaxScores.getOrDefault(category, 0) + maxWeight);
            }
        }

        // Calcular puntuaciones 0-100 por área
        Map<String, Object> areaScores = new HashMap<>();
        for (String category : cognitiveScores.keySet()) {
            int score = cognitiveScores.get(category);
            int maxScore = cognitiveMaxScores.get(category);
            BigDecimal percentage = maxScore > 0 ?
                    BigDecimal.valueOf(score * 100.0 / maxScore).setScale(2, BigDecimal.ROUND_HALF_UP) :
                    BigDecimal.ZERO;

            String level = determineLevel(percentage);

            Map<String, Object> areaData = new HashMap<>();
            areaData.put("score", percentage);
            areaData.put("level", level);
            areaScores.put(category, areaData);
        }

        // Calcular score total promedio
        BigDecimal totalScore = cognitiveScores.isEmpty() ? BigDecimal.ZERO :
                areaScores.values().stream()
                        .map(v -> ((Map<String, Object>) v).get("score"))
                        .map(s -> (BigDecimal) s)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(areaScores.size()), 2, BigDecimal.ROUND_HALF_UP);

        evaluation.setTotalScore(totalScore);
        evaluation = completedEvaluationRepository.save(evaluation);

        // Preparar resultado
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("cognitiveAreas", areaScores);
        resultData.put("overallLevel", determineLevel(totalScore));

        // Guardar resultado en JSON
        EvaluationResult result = new EvaluationResult();
        result.setEvaluation(evaluation);
        try {
            result.setResultJson(objectMapper.writeValueAsString(resultData));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing result", e);
        }
        evaluationResultRepository.save(result);

        return EvaluationMapper.toResultResponse(evaluation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationHistoryResponse> getEvaluationHistory(Integer userId) {
        List<EvaluationHistoryResponse> history = completedEvaluationRepository.findByUserIdOrderByCompletionDateDesc(userId).stream()
                .map(EvaluationMapper::toHistoryResponse)
                .collect(Collectors.toList());

        if (history.isEmpty()) {
            throw new IllegalArgumentException("There is no review history for this user.");
        }

        return history;
    }

    // Métodos auxiliares

    private BigDecimal calculateAverageScore(Map<String, Object> personalityAnalysis) {
        if (personalityAnalysis == null || !personalityAnalysis.containsKey("dimensions")) {
            return BigDecimal.ZERO;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> dimensions = (Map<String, Object>) personalityAnalysis.get("dimensions");

        if (dimensions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        double sum = dimensions.values().stream()
                .mapToDouble(v -> v instanceof Number ? ((Number) v).doubleValue() : 0.0)
                .sum();

        return BigDecimal.valueOf(sum / dimensions.size()).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String determineLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(40)) <= 0) {
            return "bajo";
        } else if (score.compareTo(BigDecimal.valueOf(70)) <= 0) {
            return "medio";
        } else {
            return "alto";
        }
    }

    private List<String> generateVocationalRecommendations(List<Map.Entry<String, BigDecimal>> topAreas) {
        List<String> recommendations = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : topAreas) {
            String area = entry.getKey();
            recommendations.add("Considera carreras en el área de " + area +
                    " donde podrás desarrollar tu potencial al máximo.");
        }

        return recommendations;
    }
}
