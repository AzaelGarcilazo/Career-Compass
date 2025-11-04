package compass.career.careercompass.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@Data
@Builder
public class CareerRequest {
    @NotBlank
    @Size(max = 200)
    @JsonProperty("Name")
    private String name;

    @Size(max = 2000)
    @JsonProperty("Description")
    private String description;

    @Min(1)
    @JsonProperty("Duration Semesters")
    private Integer durationSemesters;

    @Size(max = 2000)
    @JsonProperty("Graduate Profile")
    private String graduateProfile;

    @Size(max = 2000)
    @JsonProperty("Job Field")
    private String jobField;

    @DecimalMin("0.00")
    @JsonProperty("Average Salary")
    private BigDecimal averageSalary;
}