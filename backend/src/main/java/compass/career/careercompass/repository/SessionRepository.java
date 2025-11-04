package compass.career.careercompass.repository;

import compass.career.careercompass.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    Optional<Session> findByTokenAndActiveTrue(String token);
    Optional<Session> findByToken(String token);
}
