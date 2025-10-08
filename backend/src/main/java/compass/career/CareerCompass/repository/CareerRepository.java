package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.Career;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository extends JpaRepository<Career, Integer> {
}
