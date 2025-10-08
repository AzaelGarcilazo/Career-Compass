package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.TestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestTypeRepository extends JpaRepository<TestType, Integer> {
    Optional<TestType> findByName(String name);
}
