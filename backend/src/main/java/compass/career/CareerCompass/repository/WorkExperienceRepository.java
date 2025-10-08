package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Integer> {
    List<WorkExperience> findByUserId(Integer userId);
    Optional<WorkExperience> findByIdAndUserId(Integer id, Integer userId);
}
