package compass.career.careercompass.repository;

import compass.career.careercompass.model.AcademicInformation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcademicInformationRepository extends JpaRepository<AcademicInformation, Integer> {
    List<AcademicInformation> findByUserId(Integer userId, Pageable pageable);
    Optional<AcademicInformation> findByIdAndUserId(Integer id, Integer userId);
}
