package compass.career.careercompass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Past
    private LocalDate birthDate;

    @NotBlank
    @Size(max = 100)
    private String country;

    @NotBlank
    @Size(max = 100)
    private String city;
}
