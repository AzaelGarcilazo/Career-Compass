package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.FavoriteCareer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteCareerRepository extends JpaRepository<FavoriteCareer, Integer> {
    List<FavoriteCareer> findByUserIdAndActiveTrue(Integer userId);
    Optional<FavoriteCareer> findByUserIdAndCareerId(Integer userId, Integer careerId);
    long countByUserIdAndActiveTrue(Integer userId);
}
