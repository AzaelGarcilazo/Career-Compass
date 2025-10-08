package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginResponse {
    Integer userId;
    String name;
    String email;
    String role;
    String token;
}
