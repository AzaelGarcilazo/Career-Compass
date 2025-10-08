package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Integer> {
    Optional<EvaluationResult> findByEvaluationId(Integer evaluationId);
}
