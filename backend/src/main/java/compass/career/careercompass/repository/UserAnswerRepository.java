package compass.career.careercompass.repository;

import compass.career.careercompass.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {
    List<UserAnswer> findByEvaluationIdOrderByQuestionId(Integer evaluationId);
}
