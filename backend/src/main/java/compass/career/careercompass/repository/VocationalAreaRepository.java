package compass.career.careercompass.repository;

import compass.career.careercompass.model.VocationalArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VocationalAreaRepository extends JpaRepository<VocationalArea, Integer> {
    Optional<VocationalArea> findByName(String name);
}
