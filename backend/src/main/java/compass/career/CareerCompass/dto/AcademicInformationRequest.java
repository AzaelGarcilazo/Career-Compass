package compass.career.CareerCompass.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AcademicInformationRequest {
    @NotBlank
    @Size(max = 200)
    private String institution;

    @Size(max = 200)
    private String career;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private BigDecimal average;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean inProgress = false;
}
