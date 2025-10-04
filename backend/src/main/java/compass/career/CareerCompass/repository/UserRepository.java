package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
