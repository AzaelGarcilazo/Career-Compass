package compass.career.CareerCompass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {
    private String questionText;

    private Integer orderNumber;

    @NotEmpty
    private List<AnswerOptionRequest> options;
} 

