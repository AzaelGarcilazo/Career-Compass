package compass.career.CareerCompass.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CareerRequest {
    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 2000)
    private String description;

    @Min(1)
    private Integer durationSemesters;

    @Size(max = 2000)
    private String graduateProfile;

    @Size(max = 2000)
    private String jobField;

    @DecimalMin("0.00")
    private BigDecimal averageSalary;
}
