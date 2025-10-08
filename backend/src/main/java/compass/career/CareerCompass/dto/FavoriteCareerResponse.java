package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FavoriteCareerResponse {
    Integer id;
    Integer careerId;
    String careerName;
    String notes;
    Boolean active;
}
