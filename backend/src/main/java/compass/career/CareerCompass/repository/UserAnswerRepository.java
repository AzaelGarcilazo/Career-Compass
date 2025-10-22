package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {
    List<UserAnswer> findByEvaluationIdOrderByQuestionId(Integer evaluationId);
}
