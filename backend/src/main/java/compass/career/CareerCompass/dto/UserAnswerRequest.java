package compass.career.CareerCompass.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserAnswerRequest {
    @NotNull
    private Integer questionId;

    @NotNull
    private Integer optionId;
}
