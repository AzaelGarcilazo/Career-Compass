package compass.career.careercompass.controller;

import compass.career.careercompass.dto.CompleteProfileResponse;
import compass.career.careercompass.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Profile", description = "Endpoints for user profile management")
public class ProfileController {

    private final ProfileService profileService;
    private final AuthenticationHelper authHelper;

    @GetMapping
    @Operation(
            summary = "Get the full user profile",
            description = "Retrieves all profile information of the authenticated user."
    )
    public CompleteProfileResponse getCompleteProfile(Authentication authentication) {
        var user = authHelper.getAuthenticatedUser(authentication);
        return profileService.getCompleteProfile(user.getId());
    }
}