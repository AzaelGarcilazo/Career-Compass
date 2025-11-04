package compass.career.careercompass.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "specialization_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecializationRecommendation {

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

    @Column(name = "compatibility_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal compatibilityPercentage;
}
