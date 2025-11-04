package compass.career.careercompass.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @OneToOne(mappedBy = "credential", cascade = CascadeType.ALL)
    private User user;
}
