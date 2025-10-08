package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.AreaResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaResultRepository extends JpaRepository<AreaResult, Integer> {
    List<AreaResult> findByEvaluationIdOrderByRankingAsc(Integer evaluationId);
}
