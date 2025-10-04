package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialRepository extends JpaRepository<Credential, Integer> {
}
