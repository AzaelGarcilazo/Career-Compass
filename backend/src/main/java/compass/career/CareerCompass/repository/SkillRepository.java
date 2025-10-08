package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
    List<Skill> findByUserId(Integer userId);
    Optional<Skill> findByIdAndUserId(Integer id, Integer userId);
    Optional<Skill> findByUserIdAndSkillName(Integer userId, String skillName);
    long countByUserId(Integer userId);
}
