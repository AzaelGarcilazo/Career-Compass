package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.PasswordRecovery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, Integer> {
    Optional<PasswordRecovery> findByTokenAndUsedFalse(String token);
}