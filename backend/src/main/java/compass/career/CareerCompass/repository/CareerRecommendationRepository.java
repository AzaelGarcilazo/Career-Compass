package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.CareerRecommendation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CareerRecommendationRepository extends JpaRepository<CareerRecommendation, Integer> {

    @Query("SELECT cr FROM CareerRecommendation cr JOIN FETCH cr.career WHERE cr.user.id = :userId ORDER BY cr.compatibilityPercentage DESC")
    List<CareerRecommendation> findByUserIdOrderByCompatibilityPercentageDesc(@Param("userId") Integer userId);

    void deleteByUserId(Integer userId);
}
