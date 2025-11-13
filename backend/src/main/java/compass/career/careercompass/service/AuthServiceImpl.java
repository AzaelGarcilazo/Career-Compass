package compass.career.careercompass.service;

import compass.career.careercompass.dto.*;
import compass.career.careercompass.mapper.AuthMapper;
import compass.career.careercompass.model.*;
import compass.career.careercompass.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;
    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // Validar edad mínima
        if (Period.between(request.getBirthDate(), LocalDate.now()).getYears() < 15) {
            throw new IllegalArgumentException("User must be at least 15 years old");
        }

        // Verificar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        // Verificar que el username no exista
        if (credentialRepository.existsByUsername(request.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists");
        }

        // Buscar el rol
        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + request.getRole()));

        // Crear credenciales con contraseña encriptada
        Credential credential = new Credential();
        credential.setUsername(request.getUsername());
        credential.setPassword(passwordEncoder.encode(request.getPassword()));
        credential = credentialRepository.save(credential);

        // Crear usuario
        User user = AuthMapper.toEntity(request, credential, role);
        user = userRepository.save(user);

        // Generar token JWT
        String jwtToken = jwtService.generateToken(credential);

        return AuthMapper.toLoginResponse(user, jwtToken);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Autenticar con Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Invalid credentials"));

        // Generar token JWT
        String jwtToken = jwtService.generateToken(user.getCredential());

        return AuthMapper.toLoginResponse(user, jwtToken);
    }

    @Override
    @Transactional
    public void requestPasswordRecovery(PasswordRecoveryRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Email not found"));

        // Generar token para recuperación (esto puede seguir usando UUID para emails)
        String token = java.util.UUID.randomUUID().toString();
        PasswordRecovery recovery = new PasswordRecovery();
        recovery.setUser(user);
        recovery.setToken(token);
        recovery.setUsed(false);
        passwordRecoveryRepository.save(recovery);

        // TODO: Enviar email con el token de recuperación
    }

    @Override
    @Transactional
    public void changePassword(Integer userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Verificar contraseña antigua usando BCrypt
        if (!passwordEncoder.matches(request.getOldPassword(), user.getCredential().getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Actualizar contraseña con hash
        Credential credential = user.getCredential();
        credential.setPassword(passwordEncoder.encode(request.getNewPassword()));
        credentialRepository.save(credential);
    }

    @Override
    @Transactional
    public User updateProfile(Integer userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Validar edad mínima
        if (Period.between(request.getBirthDate(), LocalDate.now()).getYears() < 15) {
            throw new IllegalArgumentException("User must be at least 15 years old");
        }

        AuthMapper.copyToEntity(request, user);
        return userRepository.save(user);
    }


}