package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.FavoriteCareerRequest;
import compass.career.CareerCompass.dto.FavoriteCareerResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.FavoriteCareerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/favorite-careers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Favorite Careers", description = "Endpoints for managing user's favorite careers")
public class FavoriteCareerController {

    private final FavoriteCareerService careerService;
    private final AuthService authService;

    @PostMapping
    @Operation(
            summary = "Add a career to favorites",
            description = "Allows the user to mark a career as favorite for easy consultation later. Personal notes can be added about why the career is of interest. The system validates that duplicate careers are not added and allows a maximum number of favorite careers per user."
    )
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

    @DeleteMapping("/{careerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Remove a career from favorites",
            description = "Removes a career from the user's favorites list. The career is marked as inactive but not physically deleted from the database, allowing it to be reactivated later if added again."
    )
    public void removeFavoriteCareer(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer careerId) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        careerService.removeFavoriteCareer(user.getId(), careerId);
    }

    @GetMapping(value = "/favorites", params = { "page", "pageSize" })
    @Operation(
            summary = "Get favorite careers with pagination",
            description = "Retrieves the list of careers marked as favorites by the user, with pagination support. Allows organizing and navigating large numbers of favorite careers efficiently. The page and pageSize parameters must be greater than or equal to 0, and cannot both be 0 simultaneously."
    )
    public List<FavoriteCareerResponse> getFavoriteCareers(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,

            @Parameter(description = "Number of favorite careers per page", example = "10")
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: page and pageSize cannot be negative and cannot both be 0.");
        }

        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return careerService.getFavoriteCareers(user.getId(), page, pageSize);
    }
}