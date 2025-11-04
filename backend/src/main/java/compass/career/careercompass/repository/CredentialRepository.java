package compass.career.careercompass.repository;

import compass.career.careercompass.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Integer> {
    Optional<Credential> findByUsername(String username);
    boolean existsByUsername(String username);
}
