package compass.career.careercompass.controller;

import compass.career.careercompass.model.Credential;
import compass.career.careercompass.model.User;
import compass.career.careercompass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationHelper {

    private final UserRepository userRepository;

    public User getAuthenticatedUser(Authentication authentication) {
        Credential credential = (Credential) authentication.getPrincipal();

        return credential.getUser();
    }

    public String getAuthenticatedUserEmail(Authentication authentication) {
        return authentication.getName();
    }
}