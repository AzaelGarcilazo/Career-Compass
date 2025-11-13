package compass.career.careercompass.controller;

import compass.career.careercompass.dto.*;
import compass.career.careercompass.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationHelper authHelper;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user to the system",
            description = "Create a new user account using the provided data. Returns user information and JWT token."
    )
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity
                .created(URI.create("/api/v1/auth/profile"))
                .body(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate a user and log in",
            description = "Validates a user's credentials to log in. Returns user information and JWT token."
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password-recovery")
    @Operation(
            summary = "Allow password recovery",
            description = "Start the password recovery process for a user via their email."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void requestPasswordRecovery(@Valid @RequestBody PasswordRecoveryRequest request) {
        authService.requestPasswordRecovery(request);
    }

    @PutMapping("/change-password")
    @Operation(
            summary = "Update your password",
            description = "Allows a logged-in user to change their current password to a new one."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        var user = authHelper.getAuthenticatedUser(authentication);
        authService.changePassword(user.getId(), request);
    }

    @PutMapping("/profile")
    @Operation(
            summary = "Update profile information",
            description = "Allows an authenticated user to update their profile information."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        var user = authHelper.getAuthenticatedUser(authentication);
        authService.updateProfile(user.getId(), request);
    }
}