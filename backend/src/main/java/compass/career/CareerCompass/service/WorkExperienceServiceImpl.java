package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.WorkExperienceRequest;
import compass.career.CareerCompass.dto.WorkExperienceResponse;
import compass.career.CareerCompass.mapper.WorkExperienceMapper;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.model.WorkExperience;
import compass.career.CareerCompass.repository.UserRepository;
import compass.career.CareerCompass.repository.WorkExperienceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkExperienceServiceImpl implements WorkExperienceService {

    private final WorkExperienceRepository repository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WorkExperienceResponse create(Integer userId, WorkExperienceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        WorkExperience entity = WorkExperienceMapper.toEntity(request, user);
        WorkExperience saved = repository.save(entity);
        return WorkExperienceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WorkExperienceResponse update(Integer userId, Integer id, WorkExperienceRequest request) {
        WorkExperience entity = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Work experience not found"));

        WorkExperienceMapper.copyToEntity(request, entity);
        WorkExperience saved = repository.save(entity);
        return WorkExperienceMapper.toResponse(saved);
    }

    @Override
    public List<WorkExperienceResponse> findByUserId(Integer userId) {
        return repository.findByUserId(userId).stream()
                .map(WorkExperienceMapper::toResponse)
                .collect(Collectors.toList());
    }
}
