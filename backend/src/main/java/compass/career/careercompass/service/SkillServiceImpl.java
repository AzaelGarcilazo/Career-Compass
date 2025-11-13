package compass.career.careercompass.service;

import compass.career.careercompass.dto.SkillRequest;
import compass.career.careercompass.dto.SkillResponse;
import compass.career.careercompass.dto.UpdateSkillRequest;
import compass.career.careercompass.mapper.SkillMapper;
import compass.career.careercompass.model.Skill;
import compass.career.careercompass.model.User;
import compass.career.careercompass.repository.SkillRepository;
import compass.career.careercompass.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillServiceImpl implements SkillService {

    private final SkillRepository repository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SkillResponse create(Integer userId, SkillRequest request) {
        log.info("Creating skill '{}' for user {}", request.getSkillName(), userId);

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

        log.info("Skill '{}' created successfully with id {} for user {}",
                saved.getSkillName(), saved.getId(), userId);
        return SkillMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SkillResponse update(Integer userId, Integer id, UpdateSkillRequest request) {
        log.info("Updating skill {} for user {} - New name: '{}', New level: {}",
                id, userId, request.getSkillName(), request.getProficiencyLevel());

        Skill entity = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found"));

        String oldSkillName = entity.getSkillName();
        Integer oldProficiencyLevel = entity.getProficiencyLevel();

        // Si se cambia el nombre de la skill, verificar que el nuevo nombre no exista ya
        if (!oldSkillName.equalsIgnoreCase(request.getSkillName())) {
            Optional<Skill> existingSkill = repository.findByUserIdAndSkillName(userId, request.getSkillName());
            if (existingSkill.isPresent() && !existingSkill.get().getId().equals(id)) {
                throw new DataIntegrityViolationException(
                        "A skill with the name '" + request.getSkillName() + "' already exists for this user"
                );
            }
        }

        // Actualizar los campos
        entity.setSkillName(request.getSkillName());
        entity.setProficiencyLevel(request.getProficiencyLevel());

        Skill saved = repository.save(entity);

        log.info("Skill {} updated successfully for user {} - Old: '{}' (level {}), New: '{}' (level {})",
                id, userId, oldSkillName, oldProficiencyLevel,
                saved.getSkillName(), saved.getProficiencyLevel());

        return SkillMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> findByUserId(Integer userId, int page, int pageSize) {
        log.debug("Finding skills for user {} with pagination - page: {}, pageSize: {}",
                userId, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize);

        List<SkillResponse> skills = repository.findByUserId(userId, pageable).stream()
                .map(SkillMapper::toResponse)
                .collect(Collectors.toList());

        if (skills.isEmpty()) {
            throw new IllegalArgumentException("There are no skills registered for this user.");
        }

        return skills;
    }
}