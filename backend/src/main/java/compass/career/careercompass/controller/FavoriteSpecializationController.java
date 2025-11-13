package compass.career.careercompass.controller;

import compass.career.careercompass.dto.FavoriteSpecializationRequest;
import compass.career.careercompass.dto.FavoriteSpecializationResponse;
import compass.career.careercompass.service.FavoriteSpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/favorite-specializations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Favorite Specializations", description = "Endpoints for managing user's favorite specializations")
public class FavoriteSpecializationController {

    private final FavoriteSpecializationService specializationService;
    private final AuthenticationHelper authHelper;

    @PostMapping
    @Operation(
            summary = "Add a specialization to favorites",
            description = "Allows the user to mark a specialization as favorite for easy consultation later. Personal notes can be added about why the specialization is of interest."
    )
    public ResponseEntity<FavoriteSpecializationResponse> addFavoriteSpecialization(
            Authentication authentication,
            @Valid @RequestBody FavoriteSpecializationRequest request) {
        var user = authHelper.getAuthenticatedUser(authentication);
        FavoriteSpecializationResponse response = specializationService.addFavoriteSpecialization(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/specializations/favorites"))
                .body(response);
    }

    @DeleteMapping("/{specializationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Remove a specialization from favorites",
            description = "Removes a specialization from the user's favorites list. The specialization is marked as inactive but not physically deleted."
    )
    public void removeFavoriteSpecialization(
            Authentication authentication,
            @PathVariable Integer specializationId) {
        var user = authHelper.getAuthenticatedUser(authentication);
        specializationService.removeFavoriteSpecialization(user.getId(), specializationId);
    }

    @GetMapping(value = "/favorites", params = { "page", "pageSize" })
    @Operation(
            summary = "Get favorite specializations with pagination",
            description = "Retrieves the list of specializations marked as favorites by the user with pagination support."
    )
    public List<FavoriteSpecializationResponse> getFavoriteSpecializationsPaginated(
            Authentication authentication,
            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @Parameter(description = "Number of favorite specializations per page", example = "10")
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: page and pageSize cannot be negative and cannot both be 0.");
        }

        var user = authHelper.getAuthenticatedUser(authentication);
        return specializationService.getFavoriteSpecializations(user.getId(), page, pageSize);
    }
}