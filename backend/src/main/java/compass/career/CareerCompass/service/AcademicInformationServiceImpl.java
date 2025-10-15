package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.AcademicInformationRequest;
import compass.career.CareerCompass.dto.AcademicInformationResponse;
import compass.career.CareerCompass.mapper.AcademicInformationMapper;
import compass.career.CareerCompass.model.AcademicInformation;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.repository.AcademicInformationRepository;
import compass.career.CareerCompass.repository.UserRepository;
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
public class AcademicInformationServiceImpl implements AcademicInformationService {

    private final AcademicInformationRepository repository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AcademicInformationResponse create(Integer userId, AcademicInformationRequest request) {
        log.info("Creating academic information for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {
            request.validateDateLogic();
        } catch (IllegalArgumentException e) {
            log.warn("Date validation failed for user {}: {}", userId, e.getMessage());
            throw e;
        }

        log.debug("Academic information validated - inProgress: {}, endDate: {}",
                request.getInProgress(), request.getEndDate());

        AcademicInformation entity = AcademicInformationMapper.toEntity(request, user);
        AcademicInformation saved = repository.save(entity);

        log.info("Academic information created successfully with id {} for user {}", saved.getId(), userId);
        return AcademicInformationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AcademicInformationResponse update(Integer userId, Integer id, AcademicInformationRequest request) {
        log.info("Updating academic information {} for user {}", id, userId);

        AcademicInformation entity = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Academic information not found"));

        try {
            request.validateDateLogic();
        } catch (IllegalArgumentException e) {
            log.warn("Date validation failed for user {}: {}", userId, e.getMessage());
            throw e;
        }

        log.debug("Academic information validated - inProgress: {}, endDate: {}",
                request.getInProgress(), request.getEndDate());

        AcademicInformationMapper.copyToEntity(request, entity);
        AcademicInformation saved = repository.save(entity);

        log.info("Academic information {} updated successfully for user {}", id, userId);
        return AcademicInformationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicInformationResponse> findByUserId(Integer userId, int page, int pageSize) {
        log.debug("Finding academic information for user {} with pagination - page: {}, pageSize: {}",
                userId, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize);

        return repository.findByUserId(userId, pageable).stream()
                .map(AcademicInformationMapper::toResponse)
                .collect(Collectors.toList());
    }
}