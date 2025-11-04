package compass.career.careercompass.dto;

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

    private Boolean currentJob;  // El usuario puede enviarlo, pero lo validaremos en el service

    /**
     * Valida que la lógica de fechas sea consistente
     */
    public void validateDateLogic() {
        // Si hay endDate, currentJob debe ser false
        if (endDate != null && (currentJob == null || currentJob)) {
            currentJob = false;
        }

        // Si no hay endDate, currentJob debe ser true
        if (endDate == null && (currentJob == null || !currentJob)) {
            currentJob = true;
        }

        // Validar que endDate sea después de startDate
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        // Validar que las fechas no sean futuras (opcional)
        LocalDate today = LocalDate.now();
        if (startDate != null && startDate.isAfter(today)) {
            throw new IllegalArgumentException("Start date cannot be in the future");
        }
        if (endDate != null && endDate.isAfter(today)) {
            throw new IllegalArgumentException("End date cannot be in the future");
        }
    }
}