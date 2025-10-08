package compass.career.CareerCompass.repository;

import compass.career.CareerCompass.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Integer> {
    List<Test> findByActiveTrue();

    @Query("SELECT t FROM Test t JOIN FETCH t.testType WHERE t.id = :id AND t.active = true")
    Optional<Test> findByIdAndActiveTrue(@Param("id") Integer id);

    @Query("SELECT t FROM Test t JOIN FETCH t.testType tt WHERE tt.name = :typeName AND t.active = true")
    Optional<Test> findByTestTypeNameAndActiveTrue(@Param("typeName") String typeName);
}
