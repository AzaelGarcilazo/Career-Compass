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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicInformationServiceImpl implements AcademicInformationService {

    private final AcademicInformationRepository repository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AcademicInformationResponse create(Integer userId, AcademicInformationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        AcademicInformation entity = AcademicInformationMapper.toEntity(request, user);
        AcademicInformation saved = repository.save(entity);
        return AcademicInformationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AcademicInformationResponse update(Integer userId, Integer id, AcademicInformationRequest request) {
        AcademicInformation entity = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Academic information not found"));

        AcademicInformationMapper.copyToEntity(request, entity);
        AcademicInformation saved = repository.save(entity);
        return AcademicInformationMapper.toResponse(saved);
    }

    @Override
    public List<AcademicInformationResponse> findByUserId(Integer userId) {
        return repository.findByUserId(userId).stream()
                .map(AcademicInformationMapper::toResponse)
                .collect(Collectors.toList());
    }
}
