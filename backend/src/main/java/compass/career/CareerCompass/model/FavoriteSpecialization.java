package compass.career.CareerCompass.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorite_specializations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "specialization_area_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteSpecialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "specialization_area_id", nullable = false)
    private SpecializationArea specializationArea;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "active")
    private Boolean active = true;
}
