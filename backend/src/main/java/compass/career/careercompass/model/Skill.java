package compass.career.careercompass.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skills", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "skill_name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Column(name = "proficiency_level", nullable = false)
    private Integer proficiencyLevel;
}
