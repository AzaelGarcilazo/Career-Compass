package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder

public class FavoriteSpecializationResponse {
    Integer id;
    Integer specializationAreaId;
    String specializationName;
    String notes;
    Boolean active;    
}
