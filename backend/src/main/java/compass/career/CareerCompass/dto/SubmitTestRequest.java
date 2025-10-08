package compass.career.CareerCompass.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SubmitTestRequest {
    @NotNull
    private Integer testId;

    @NotEmpty
    private List<UserAnswerRequest> answers;
}
