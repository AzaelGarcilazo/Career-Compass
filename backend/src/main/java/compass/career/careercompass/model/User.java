package compass.career.careercompass.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "credential_id", nullable = false, unique = true)
    private Credential credential;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Session> sessions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PasswordRecovery> passwordRecoveries;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AcademicInformation> academicInformations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WorkExperience> workExperiences;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Skill> skills;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CompletedEvaluation> completedEvaluations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CareerRecommendation> careerRecommendations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FavoriteCareer> favoriteCareers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SpecializationRecommendation> specializationRecommendations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FavoriteSpecialization> favoriteSpecializations;
}