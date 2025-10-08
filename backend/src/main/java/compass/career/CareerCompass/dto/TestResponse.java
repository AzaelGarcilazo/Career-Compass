package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TestResponse {
    Integer id;
    String name;
    String description;
    String testType;
    Integer questionsToShow;
    List<QuestionResponse> questions;
}
