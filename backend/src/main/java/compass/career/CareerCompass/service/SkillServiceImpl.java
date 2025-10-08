package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.SkillRequest;
import compass.career.CareerCompass.dto.SkillResponse;
import compass.career.CareerCompass.mapper.SkillMapper;
import compass.career.CareerCompass.model.Skill;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.repository.SkillRepository;
import compass.career.CareerCompass.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository repository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SkillResponse create(Integer userId, SkillRequest request) {
        // Validar máximo 50 habilidades
        if (repository.countByUserId(userId) >= 50) {
            throw new IllegalArgumentException("Maximum 50 skills allowed per user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Verificar que no exista la misma habilidad
        if (repository.findByUserIdAndSkillName(userId, request.getSkillName()).isPresent()) {
            throw new DataIntegrityViolationException("Skill already exists for this user");
        }

        Skill entity = SkillMapper.toEntity(request, user);
        Skill saved = repository.save(entity);
        return SkillMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SkillResponse updateProficiencyLevel(Integer userId, Integer id, Integer newLevel) {
        Skill entity = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found"));

        // Validar que solo se incremente o decremente un nivel a la vez
        int currentLevel = entity.getProficiencyLevel();
        if (Math.abs(newLevel - currentLevel) != 1) {
            throw new IllegalArgumentException("Can only increment or decrement proficiency level by one");
        }

        // Validar rango 1-5
        if (newLevel < 1 || newLevel > 5) {
            throw new IllegalArgumentException("Proficiency level must be between 1 and 5");
        }

        entity.setProficiencyLevel(newLevel);
        Skill saved = repository.save(entity);
        return SkillMapper.toResponse(saved);
    }

    @Override
    public List<SkillResponse> findByUserId(Integer userId) {
        return repository.findByUserId(userId).stream()
                .map(SkillMapper::toResponse)
                .collect(Collectors.toList());
    }
}

