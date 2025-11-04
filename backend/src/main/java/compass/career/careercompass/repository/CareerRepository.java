package compass.career.careercompass.repository;

import compass.career.careercompass.model.Career;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository extends JpaRepository<Career, Integer> {
}
