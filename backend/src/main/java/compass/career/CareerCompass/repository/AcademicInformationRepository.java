package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.AcademicInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcademicInformationRepository extends JpaRepository<AcademicInformation, Integer> {
    List<AcademicInformation> findByUserId(Integer userId);
    Optional<AcademicInformation> findByIdAndUserId(Integer id, Integer userId);
}
