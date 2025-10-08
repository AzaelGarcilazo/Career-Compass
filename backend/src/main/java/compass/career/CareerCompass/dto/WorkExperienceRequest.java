package compass.career.CareerCompass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkExperienceRequest {
    @NotBlank
    @Size(max = 200)
    private String company;

    @NotBlank
    @Size(max = 150)
    private String position;

    @NotBlank
    @Size(min = 50)
    private String description;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean currentJob = false;
}
