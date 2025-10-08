package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class EvaluationResultResponse {
    Integer evaluationId;
    String testName;
    String testType;
    LocalDateTime completionDate;
    BigDecimal totalScore;
    Object resultDetails;
}
