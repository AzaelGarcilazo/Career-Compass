package compass.career.careercompass.repository;

import compass.career.careercompass.model.TestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestTypeRepository extends JpaRepository<TestType, Integer> {
    Optional<TestType> findByName(String name);
}
