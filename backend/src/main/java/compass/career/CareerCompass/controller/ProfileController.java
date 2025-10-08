package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.CompleteProfileResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.ProfileService;
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
    public CompleteProfileResponse getCompleteProfile(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return profileService.getCompleteProfile(user.getId());
    }
}
