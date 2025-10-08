package compass.career.CareerCompass.mapper;

import compass.career.CareerCompass.dto.LoginResponse;
import compass.career.CareerCompass.dto.RegisterRequest;
import compass.career.CareerCompass.dto.UpdateProfileRequest;
import compass.career.CareerCompass.model.Credential;
import compass.career.CareerCompass.model.Role;
import compass.career.CareerCompass.model.User;

public final class AuthMapper {

    public static User toEntity(RegisterRequest dto, Credential credential, Role role) {
        if (dto == null || credential == null || role == null)
            return null;

        User user = new User();
        user.setCredential(credential);
        user.setRole(role);
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setBirthDate(dto.getBirthDate());
        user.setCountry(dto.getCountry());
        user.setCity(dto.getCity());

        return user;
    }

    public static LoginResponse toLoginResponse(User user, String token) {
        if (user == null)
            return null;

        return LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .token(token)
                .build();
    }

    public static void copyToEntity(UpdateProfileRequest dto, User entity) {
        if (dto == null || entity == null)
            return;

        entity.setName(dto.getName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setCountry(dto.getCountry());
        entity.setCity(dto.getCity());
    }
}
