package compass.career.careercompass.service;

import compass.career.careercompass.dto.WorkExperienceRequest;
import compass.career.careercompass.dto.WorkExperienceResponse;
import compass.career.careercompass.mapper.WorkExperienceMapper;
import compass.career.careercompass.model.User;
import compass.career.careercompass.model.WorkExperience;
import compass.career.careercompass.repository.UserRepository;
import compass.career.careercompass.repository.WorkExperienceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkExperienceServiceImpl implements WorkExperienceService {

    private final WorkExperienceRepository repository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WorkExperienceResponse create(Integer userId, WorkExperienceRequest request) {
        log.info("Creating work experience for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {
            request.validateDateLogic();
        } catch (IllegalArgumentException e) {
            log.warn("Date validation failed for user {}: {}", userId, e.getMessage());
            throw e;
        }

        log.debug("Work experience validated - currentJob: {}, endDate: {}",
                request.getCurrentJob(), request.getEndDate());

        WorkExperience entity = WorkExperienceMapper.toEntity(request, user);
        WorkExperience saved = repository.save(entity);

        log.info("Work experience created successfully with id {} for user {}", saved.getId(), userId);
        return WorkExperienceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WorkExperienceResponse update(Integer userId, Integer id, WorkExperienceRequest request) {
        log.info("Updating work experience {} for user {}", id, userId);

        WorkExperience entity = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Work experience not found"));

        try {
            request.validateDateLogic();
        } catch (IllegalArgumentException e) {
            log.warn("Date validation failed for user {}: {}", userId, e.getMessage());
            throw e;
        }

        log.debug("Work experience validated - currentJob: {}, endDate: {}",
                request.getCurrentJob(), request.getEndDate());

        WorkExperienceMapper.copyToEntity(request, entity);
        WorkExperience saved = repository.save(entity);

        log.info("Work experience {} updated successfully for user {}", id, userId);
        return WorkExperienceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkExperienceResponse> findByUserId(Integer userId, int page, int pageSize) {
        log.debug("Finding work experiences for user {} with pagination - page: {}, pageSize: {}",
                userId, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize);

        List<WorkExperienceResponse> workExperiences = repository.findByUserId(userId, pageable).stream()
                .map(WorkExperienceMapper::toResponse)
                .collect(Collectors.toList());

        if (workExperiences.isEmpty()) {
            throw new IllegalArgumentException("There is no work experience registered for this user.");
        }

        return workExperiences;
    }
}