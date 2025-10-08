package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.SpecializationArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecializationAreaRepository extends JpaRepository<SpecializationArea, Integer> {
    List<SpecializationArea> findByCareerId(Integer careerId);
}
