package compass.career.careercompass.repository;

import compass.career.careercompass.model.FavoriteSpecialization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteSpecializationRepository extends JpaRepository<FavoriteSpecialization, Integer> {
    List<FavoriteSpecialization> findByUserIdAndActiveTrue(Integer userId, Pageable pageable);
    Optional<FavoriteSpecialization> findByUserIdAndSpecializationAreaId(Integer userId, Integer specializationAreaId);
    long countByUserIdAndActiveTrue(Integer userId);
}
