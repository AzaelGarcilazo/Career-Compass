package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.*;
import compass.career.CareerCompass.mapper.AdminMapper;
import compass.career.CareerCompass.mapper.TestMapper;
import compass.career.CareerCompass.model.*;
import compass.career.CareerCompass.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final TestRepository testRepository;
    private final TestTypeRepository testTypeRepository;
    private final QuestionRepository questionRepository;
    private final CareerRepository careerRepository;
    private final SpecializationAreaRepository specializationAreaRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<CareerResponse> getAllCareers() {
        List<CareerResponse> careers = careerRepository.findAll().stream()
                .map(AdminMapper::toCareerResponse)
                .collect(Collectors.toList());

        if (careers.isEmpty()) {
            throw new IllegalArgumentException("No hay carreras disponibles en el sistema");
        }

        return careers;
    }

    @Override
    @Transactional(readOnly = true)
    public CareerResponse getCareerDetails(Integer careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("The requested career has not been found"));

        return AdminMapper.toCareerResponse(career);
    }

    @Override
    @Transactional
    public CareerResponse createCareer(CareerRequest request) {
        Career career = AdminMapper.toCareerEntity(request);
        Career saved = careerRepository.save(career);
        return AdminMapper.toCareerResponse(saved);
    }

    @Override
    @Transactional
    public CareerResponse updateCareer(Integer careerId, CareerRequest request) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("The career to update has not been found"));

        AdminMapper.copyToCareerEntity(request, career);
        Career saved = careerRepository.save(career);
        return AdminMapper.toCareerResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecializationAreaResponse> getAllSpecializations() {
        List<SpecializationAreaResponse> specializations = specializationAreaRepository.findAll().stream()
                .map(AdminMapper::toSpecializationResponse)
                .collect(Collectors.toList());

        if (specializations.isEmpty()) {
            throw new IllegalArgumentException("No hay especializaciones disponibles en el sistema");
        }

        return specializations;
    }

    @Override
    @Transactional(readOnly = true)
    public SpecializationAreaResponse getSpecializationDetails(Integer specializationId) {
        SpecializationArea specialization = specializationAreaRepository.findById(specializationId)
                .orElseThrow(() -> new EntityNotFoundException("The requested specialization area has not been found"));

        return AdminMapper.toSpecializationResponse(specialization);
    }

    @Override
    @Transactional
    public SpecializationAreaResponse createSpecialization(SpecializationAreaRequest request) {
        Career career = careerRepository.findById(request.getCareerId())
                .orElseThrow(() -> new EntityNotFoundException("The entered career has not been found"));

        SpecializationArea specialization = AdminMapper.toSpecializationEntity(request, career);
        SpecializationArea saved = specializationAreaRepository.save(specialization);
        return AdminMapper.toSpecializationResponse(saved);
    }

    @Override
    @Transactional
    public SpecializationAreaResponse updateSpecialization(Integer specializationId, SpecializationAreaRequest request) {
        SpecializationArea specialization = specializationAreaRepository.findById(specializationId)
                .orElseThrow(() -> new EntityNotFoundException("The specialization area to update has not been found"));

        Career career = careerRepository.findById(request.getCareerId())
                .orElseThrow(() -> new EntityNotFoundException("The entered career has not been found"));

        AdminMapper.copyToSpecializationEntity(request, specialization, career);
        SpecializationArea saved = specializationAreaRepository.save(specialization);
        return AdminMapper.toSpecializationResponse(saved);
    }
}
