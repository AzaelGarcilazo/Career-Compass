package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.CompletedEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompletedEvaluationRepository extends JpaRepository<CompletedEvaluation, Integer> {
    List<CompletedEvaluation> findByUserIdOrderByCompletionDateDesc(Integer userId);

    @Query("SELECT ce FROM CompletedEvaluation ce JOIN FETCH ce.test t JOIN FETCH t.testType WHERE ce.id = :id AND ce.user.id = :userId")
    Optional<CompletedEvaluation> findByIdAndUserId(@Param("id") Integer id, @Param("userId") Integer userId);
}
