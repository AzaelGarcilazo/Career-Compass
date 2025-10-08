package compass.career.CareerCompass.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FavoriteCareerRequest {
    @NotNull
    private Integer careerId;

    @Size(max = 1000)
    private String notes;
}
