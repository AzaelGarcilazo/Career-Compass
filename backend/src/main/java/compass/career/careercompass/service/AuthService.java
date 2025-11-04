package compass.career.careercompass.service;

import compass.career.careercompass.dto.*;
import compass.career.careercompass.model.User;

public interface AuthService {
    LoginResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String token);
    void requestPasswordRecovery(PasswordRecoveryRequest request);
    void changePassword(Integer userId, ChangePasswordRequest request);
    User updateProfile(Integer userId, UpdateProfileRequest request);
    User getUserFromToken(String token);
}

