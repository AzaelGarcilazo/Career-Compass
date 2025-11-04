package compass.career.careercompass.dto;

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

    private Boolean inProgress;  // El usuario puede enviarlo, pero lo validaremos en el service

    /**
     * Valida que la lógica de fechas sea consistente
     */
    public void validateDateLogic() {
        // Si hay endDate, inProgress debe ser false
        if (endDate != null && (inProgress == null || inProgress)) {
            inProgress = false;
        }

        // Si no hay endDate, inProgress debe ser true
        if (endDate == null && (inProgress == null || !inProgress)) {
            inProgress = true;
        }

        // Validar que endDate sea después de startDate
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        // Validar que startDate no sea futura
        LocalDate today = LocalDate.now();
        if (startDate != null && startDate.isAfter(today)) {
            throw new IllegalArgumentException("Start date cannot be in the future");
        }

        // endDate puede ser futura para estudios proyectados
    }
}