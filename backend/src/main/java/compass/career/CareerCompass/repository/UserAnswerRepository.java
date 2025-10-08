package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {
}
