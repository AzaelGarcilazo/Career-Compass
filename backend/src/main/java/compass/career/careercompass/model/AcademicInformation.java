package compass.career.careercompass.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "academic_information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "institution", nullable = false, length = 200)
    private String institution;

    @Column(name = "career", length = 200)
    private String career;

    @Column(name = "average", precision = 5, scale = 2)
    private BigDecimal average;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "in_progress")
    private Boolean inProgress = false;
}
