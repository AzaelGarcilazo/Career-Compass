package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
