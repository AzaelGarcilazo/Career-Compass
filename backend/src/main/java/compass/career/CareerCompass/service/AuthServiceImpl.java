package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.*;
import compass.career.CareerCompass.mapper.AuthMapper;
import compass.career.CareerCompass.model.*;
import compass.career.CareerCompass.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;
    private final SessionRepository sessionRepository;
    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final PasswordEncoder passwordEncoder;

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

        // Crear sesión
        String token = UUID.randomUUID().toString();
        Session session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setActive(true);
        sessionRepository.save(session);

        return AuthMapper.toLoginResponse(user, token);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Invalid credentials"));

        // Verificar contraseña usando BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getCredential().getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Crear nueva sesión
        String token = UUID.randomUUID().toString();
        Session session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setActive(true);
        sessionRepository.save(session);

        return AuthMapper.toLoginResponse(user, token);
    }

    @Override
    @Transactional
    public void logout(String token) {
        Session session = sessionRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        session.setActive(false);
        sessionRepository.save(session);
    }

    @Override
    @Transactional
    public void requestPasswordRecovery(PasswordRecoveryRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Email not found"));

        String token = UUID.randomUUID().toString();
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

    @Override
    public User getUserFromToken(String token) {
        Session session = sessionRepository.findByTokenAndActiveTrue(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid or expired token"));
        return session.getUser();
    }
}