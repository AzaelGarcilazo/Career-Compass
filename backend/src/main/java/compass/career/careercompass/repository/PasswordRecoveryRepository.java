package compass.career.careercompass.repository;

import compass.career.careercompass.model.PasswordRecovery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, Integer> {
    Optional<PasswordRecovery> findByTokenAndUsedFalse(String token);
}