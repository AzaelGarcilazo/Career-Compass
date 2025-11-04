package compass.career.careercompass.repository;

import compass.career.careercompass.model.AreaResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaResultRepository extends JpaRepository<AreaResult, Integer> {
    List<AreaResult> findByEvaluationIdOrderByRankingAsc(Integer evaluationId);
}
