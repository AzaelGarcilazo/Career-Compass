package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.FavoriteSpecializationRequest;
import compass.career.CareerCompass.dto.FavoriteSpecializationResponse;
import compass.career.CareerCompass.dto.SpecializationDetailResponse;
import compass.career.CareerCompass.dto.SpecializationRecommendationResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.SpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/specializations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SpecializationController {

    private final SpecializationService specializationService;
    private final AuthService authService;

    @GetMapping("/recommendations")
    public List<SpecializationRecommendationResponse> getRecommendedSpecializations(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return specializationService.getRecommendedSpecializations(user.getId());
    }

    @GetMapping("/{specializationId}")
    public SpecializationDetailResponse getSpecializationDetails(@PathVariable Integer specializationId) {
        return specializationService.getSpecializationDetails(specializationId);
    }

    @PostMapping("/favorites")
    public ResponseEntity<FavoriteSpecializationResponse> addFavoriteSpecialization(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody FavoriteSpecializationRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        FavoriteSpecializationResponse response = specializationService.addFavoriteSpecialization(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/specializations/favorites"))
                .body(response);
    }

    @DeleteMapping("/favorites/{specializationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavoriteSpecialization(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer specializationId) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        specializationService.removeFavoriteSpecialization(user.getId(), specializationId);
    }

    @GetMapping(value = "/favorites/pagination", params = { "page", "pageSize" })
    @Operation(summary = "Get favorite specializations with pagination")
    public List<FavoriteSpecializationResponse> getFavoriteSpecializationsPaginated(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: page and pageSize cannot be negative and cannot both be 0.");
        }

        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return specializationService.getFavoriteSpecializations(user.getId(), page, pageSize);
    }
}
