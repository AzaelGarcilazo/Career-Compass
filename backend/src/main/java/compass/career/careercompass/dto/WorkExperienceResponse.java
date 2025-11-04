package compass.career.careercompass.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class WorkExperienceResponse {
    Integer id;
    String company;
    String position;
    String description;
    LocalDate startDate;
    LocalDate endDate;
    Boolean currentJob;
}
