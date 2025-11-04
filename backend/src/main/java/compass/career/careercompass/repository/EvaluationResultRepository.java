package compass.career.careercompass.repository;

import compass.career.careercompass.model.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Integer> {
    Optional<EvaluationResult> findByEvaluationId(Integer evaluationId);
}
