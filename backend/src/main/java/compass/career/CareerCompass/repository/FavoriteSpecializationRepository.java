package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.FavoriteSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteSpecializationRepository extends JpaRepository<FavoriteSpecialization, Integer> {
    List<FavoriteSpecialization> findByUserIdAndActiveTrue(Integer userId);
    Optional<FavoriteSpecialization> findByUserIdAndSpecializationAreaId(Integer userId, Integer specializationAreaId);
    long countByUserIdAndActiveTrue(Integer userId);
}
