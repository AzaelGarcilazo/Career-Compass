package compass.career.careercompass.repository;

import compass.career.careercompass.model.WorkExperience;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Integer> {
    List<WorkExperience> findByUserId(Integer userId, Pageable pageable);
    Optional<WorkExperience> findByIdAndUserId(Integer id, Integer userId);
}
