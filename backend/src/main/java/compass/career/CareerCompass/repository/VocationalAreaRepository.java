package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.VocationalArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VocationalAreaRepository extends JpaRepository<VocationalArea, Integer> {
    Optional<VocationalArea> findByName(String name);
}
