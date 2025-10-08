package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class AcademicInformationResponse {
    Integer id;
    String institution;
    String career;
    BigDecimal average;
    LocalDate startDate;
    LocalDate endDate;
    Boolean inProgress;
}
