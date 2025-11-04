package compass.career.careercompass.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateSkillRequest {
    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name cannot exceed 100 characters")
    private String skillName;

    @NotNull(message = "Proficiency level is required")
    @Min(value = 1, message = "Proficiency level must be at least 1")
    @Max(value = 5, message = "Proficiency level cannot exceed 5")
    private Integer proficiencyLevel;
}