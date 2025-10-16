package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.*;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario al sistema",
    description = "Crea una nueva cuenta de usuario a partir de los datos proporcionados.")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity
                .created(URI.create("/api/v1/auth/profile"))
                .body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar un usuario e iniciar sesión",
    description = "Valida las credenciales de un usuario para iniciar sesion y poder continuar."
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión del usuario",
    description = "Invalida el token de autenticación actual del usuario para cerrar su sesión de forma segura."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String token) {
        // Remover "Bearer " del token si existe
        String cleanToken = token.replace("Bearer ", "");
        authService.logout(cleanToken);
    }

    @PostMapping("/password-recovery")
    @Operation(summary = "Permitir la recuperación de contraseña",
    description = "Inicia el proceso de recuperación de contraseña para un usuario a través de su correo electrónico."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void requestPasswordRecovery(@Valid @RequestBody PasswordRecoveryRequest request) {
        authService.requestPasswordRecovery(request);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Actualizar la contraseña",
        description = "Permite a un usuario que ha iniciado sesión cambiar su contraseña actual por una nueva."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        authService.changePassword(user.getId(), request);
    }

    @PutMapping("/profile")
    @Operation(summary = "Actualizar la información del perfil",
    description = "Permite a un usuario autenticado actualizar la información de su perfil."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdateProfileRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        authService.updateProfile(user.getId(), request);
    }
}
