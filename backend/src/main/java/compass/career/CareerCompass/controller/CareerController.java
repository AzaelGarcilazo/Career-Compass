package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.*;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.CareerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/careers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Careers", description = "Endpoints for university career management")
public class CareerController {

    private final CareerService careerService;
    private final AuthService authService;

    @PostMapping("/recommendations")
    @Operation(
            summary = "Get personalized career recommendations",
            description = "Generates or retrieves career recommendations based on the user's evaluation results (personality, vocational interests, and cognitive skills). Uses Groq AI to generate personalized recommendations for the first time. Recommendations are stored in cache for 1 hour and in the database permanently."
    )
    public List<CareerRecommendationResponse> getRecommendedCareers(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return careerService.getRecommendedCareers(user.getId());
    }

    @GetMapping("/details/{careerId}")
    @Operation(
            summary = "Get complete career details",
            description = "Retrieves detailed information about a specific career, including description, duration, average salary, required skills, job opportunities, and social media data (Reddit) about the career."
    )
    public CareerDetailResponse getCareerDetails(@PathVariable Integer careerId) {
        return careerService.getCareerDetails(careerId);
    }

    @GetMapping
    @Operation(
            summary = "List all available careers with pagination",
            description = "Retrieves the catalog of university careers available in the system with pagination support. Careers are sorted alphabetically by name. The page and pageSize parameters control the amount of results returned."
    )
    public List<CareerResponse> getAllCareers(
            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,

            @Parameter(description = "Number of careers per page", example = "10")
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        return careerService.getAllCareers(page, pageSize);
    }

    @GetMapping("/{careerId}")
    @Operation(
            summary = "Get basic career information by ID",
            description = "Retrieves the basic data of a specific career using its unique identifier."
    )
    public CareerResponse getCareerById(@PathVariable Integer careerId) {
        return careerService.getCareerById(careerId);
    }

    @PostMapping
    @Operation(
            summary = "Create a new career (Admin)",
            description = "Registers a new career in the system with all its academic and professional information. Admin endpoint."
    )
    public ResponseEntity<CareerResponse> createCareer(@Valid @RequestBody CareerRequest request) {
        CareerResponse response = careerService.createCareer(request);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/careers/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{careerId}")
    @Operation(
            summary = "Update an existing career (Admin)",
            description = "Modifies the information of a career registered in the system. Admin endpoint."
    )
    public CareerResponse updateCareer(
            @PathVariable Integer careerId,
            @Valid @RequestBody CareerRequest request) {
        return careerService.updateCareer(careerId, request);
    }
}