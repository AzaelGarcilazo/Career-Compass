package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    Optional<Session> findByTokenAndActiveTrue(String token);
    Optional<Session> findByToken(String token);
}
