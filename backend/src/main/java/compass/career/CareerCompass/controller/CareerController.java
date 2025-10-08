package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.CareerDetailResponse;
import compass.career.CareerCompass.dto.CareerRecommendationResponse;
import compass.career.CareerCompass.dto.FavoriteCareerRequest;
import compass.career.CareerCompass.dto.FavoriteCareerResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.CareerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/careers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class CareerController {

    private final CareerService careerService;
    private final AuthService authService;

    @GetMapping("/recommendations")
    public List<CareerRecommendationResponse> getRecommendedCareers(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return careerService.getRecommendedCareers(user.getId());
    }

    @GetMapping("/{careerId}")
    public CareerDetailResponse getCareerDetails(@PathVariable Integer careerId) {
        return careerService.getCareerDetails(careerId);
    }

    @PostMapping("/favorites")
    public ResponseEntity<FavoriteCareerResponse> addFavoriteCareer(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody FavoriteCareerRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        FavoriteCareerResponse response = careerService.addFavoriteCareer(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/careers/favorites"))
                .body(response);
    }

    @DeleteMapping("/favorites/{careerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavoriteCareer(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer careerId) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        careerService.removeFavoriteCareer(user.getId(), careerId);
    }

    @GetMapping("/favorites")
    public List<FavoriteCareerResponse> getFavoriteCareers(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return careerService.getFavoriteCareers(user.getId());
    }
}
