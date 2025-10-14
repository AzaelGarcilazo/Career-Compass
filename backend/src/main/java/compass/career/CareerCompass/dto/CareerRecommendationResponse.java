package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CareerRecommendationResponse {
    Integer id;
    String name;
    String description;
    BigDecimal compatibilityPercentage;
    Integer durationSemesters;
    BigDecimal averageSalary;
}