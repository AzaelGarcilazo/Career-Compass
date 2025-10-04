package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Integer> {
}
