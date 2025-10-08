package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AnswerOptionResponse {
    Integer id;
    String optionText;
}
