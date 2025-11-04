package compass.career.careercompass.controller;

import compass.career.careercompass.dto.FavoriteSpecializationRequest;
import compass.career.careercompass.dto.FavoriteSpecializationResponse;
import compass.career.careercompass.model.User;
import compass.career.careercompass.service.AuthService;
import compass.career.careercompass.service.FavoriteSpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/favorite-specializations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class FavoriteSpecializationController {

    private final FavoriteSpecializationService specializationService;
    private final AuthService authService;

    @PostMapping
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

    @DeleteMapping("/{specializationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavoriteSpecialization(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer specializationId) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        specializationService.removeFavoriteSpecialization(user.getId(), specializationId);
    }

    @GetMapping(value = "/favorites", params = { "page", "pageSize" })
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
