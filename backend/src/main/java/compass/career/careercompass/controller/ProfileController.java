package compass.career.careercompass.controller;

import compass.career.careercompass.dto.CompleteProfileResponse;
import compass.career.careercompass.model.User;
import compass.career.careercompass.service.AuthService;
import compass.career.careercompass.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ProfileController {

    private final ProfileService profileService;
    private final AuthService authService;

    @GetMapping
    @Operation(summary = "Get the full user profile",
    description = "Retrieves all profile information of the authenticated user."
    )
    public CompleteProfileResponse getCompleteProfile(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return profileService.getCompleteProfile(user.getId());
    }
}
