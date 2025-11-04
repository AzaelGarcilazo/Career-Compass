package compass.career.careercompass.service;

import compass.career.careercompass.dto.CreateTestRequest;
import compass.career.careercompass.dto.TestListResponse;
import compass.career.careercompass.dto.TestResponse;
import compass.career.careercompass.mapper.AdminMapper;
import compass.career.careercompass.mapper.TestMapper;
import compass.career.careercompass.model.Question;
import compass.career.careercompass.model.Test;
import compass.career.careercompass.model.TestType;
import compass.career.careercompass.repository.QuestionRepository;
import compass.career.careercompass.repository.TestRepository;
import compass.career.careercompass.repository.TestTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TestTypeRepository testTypeRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TestListResponse> getAllTests() {
        List<TestListResponse> tests = testRepository.findAll().stream()
                .map(AdminMapper::toTestListResponse)
                .collect(Collectors.toList());

        if (tests.isEmpty()) {
            throw new IllegalArgumentException("No hay tests disponibles en el sistema");
        }

        return tests;
    }

    @Override
    @Transactional(readOnly = true)
    public TestResponse getTestDetails(Integer testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("The requested test has not been found"));

        // Cargar todas las preguntas activas (no aleatorias para admin)
        List<Question> questions = questionRepository.findByTestIdAndActiveTrue(test.getId());
        test.setQuestions(questions);

        return TestMapper.toResponse(test);
    }

    @Override
    @Transactional
    public TestResponse createTest(CreateTestRequest request) {
        // Validar mínimo 100 preguntas
        if (request.getQuestions() == null || request.getQuestions().size() < 100) {
            throw new IllegalArgumentException("Test must have at least 100 questions");
        }

        TestType testType = testTypeRepository.findById(request.getTestTypeId())
                .orElseThrow(() -> new EntityNotFoundException("The test type has not been found"));

        Test test = AdminMapper.toTestEntity(request, testType);
        Test saved = testRepository.save(test);

        return TestMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TestResponse updateTest(Integer testId, CreateTestRequest request) {
        // Validar mínimo 100 preguntas
        if (request.getQuestions() == null || request.getQuestions().size() < 100) {
            throw new IllegalArgumentException("Test must have at least 100 questions");
        }

        Test existingTest = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("The test to update has not been found"));

        TestType testType = testTypeRepository.findById(request.getTestTypeId())
                .orElseThrow(() -> new EntityNotFoundException("The test type has not been found"));

        // Actualizar test
        existingTest.setTestType(testType);
        existingTest.setName(request.getName());
        existingTest.setDescription(request.getDescription());
        existingTest.setQuestionsToShow(request.getQuestionsToShow());

        // Desactivar preguntas antiguas
        if (existingTest.getQuestions() != null) {
            for (Question q : existingTest.getQuestions()) {
                q.setActive(false);
            }
        }

        // Agregar nuevas preguntas
        Test newTestData = AdminMapper.toTestEntity(request, testType);
        existingTest.getQuestions().addAll(newTestData.getQuestions());

        Test saved = testRepository.save(existingTest);
        return TestMapper.toResponse(saved);
    }
}