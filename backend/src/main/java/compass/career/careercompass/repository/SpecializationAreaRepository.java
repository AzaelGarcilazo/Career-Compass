package compass.career.careercompass.repository;

import compass.career.careercompass.model.SpecializationArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecializationAreaRepository extends JpaRepository<SpecializationArea, Integer> {
    List<SpecializationArea> findByCareerId(Integer careerId);
}
