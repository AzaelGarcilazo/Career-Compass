package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class QuestionResponse {
    Integer id;
    String questionText;
    Integer orderNumber;
    List<AnswerOptionResponse> options;
}
