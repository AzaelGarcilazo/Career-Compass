package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CareerDetailResponse {
    Integer id;
    String name;
    String description;
    Integer durationSemesters;
    String graduateProfile;
    String jobField;
    BigDecimal averageSalary;
    Object socialMediaData;
}
