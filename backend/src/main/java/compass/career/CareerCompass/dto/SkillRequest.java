package compass.career.CareerCompass.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillRequest {
    @NotBlank
    @Size(max = 100)
    private String skillName;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer proficiencyLevel;
}