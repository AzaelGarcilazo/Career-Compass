package compass.career.careercompass.controller;

import compass.career.careercompass.dto.*;
import compass.career.careercompass.service.SpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/specializations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Specializations", description = "Endpoints for academic specialization management")
public class SpecializationController {

    private final SpecializationService specializationService;
    private final AuthenticationHelper authHelper;

    @PostMapping("/recommendations")
    @Operation(
            summary = "Get personalized specialization recommendations",
            description = "Generates or retrieves specialization recommendations based on the user's evaluation results, skills, and profile. Uses Groq AI to generate personalized recommendations."
    )
    public List<SpecializationRecommendationResponse> getRecommendedSpecializations(Authentication authentication) {
        var user = authHelper.getAuthenticatedUser(authentication);
        return specializationService.getRecommendedSpecializations(user.getId());
    }

    @GetMapping("/details/{specializationId}")
    @Operation(
            summary = "Get complete specialization details",
            description = "Retrieves detailed information about a specific specialization including description, application fields, job projection, and social media data."
    )
    public SpecializationDetailResponse getSpecializationDetails(@PathVariable Integer specializationId) {
        return specializationService.getSpecializationDetails(specializationId);
    }

    @GetMapping
    @Operation(
            summary = "List all available specializations",
            description = "Retrieves the catalog of academic specializations available in the system."
    )
    public List<SpecializationAreaResponse> getAllSpecializations() {
        return specializationService.getAllSpecializations();
    }

    @GetMapping("/{specializationId}")
    @Operation(
            summary = "Get basic specialization information by ID",
            description = "Retrieves the basic data of a specific specialization using its unique identifier."
    )
    public SpecializationAreaResponse getSpecializationById(@PathVariable Integer specializationId) {
        return specializationService.getSpecializationById(specializationId);
    }

    @PostMapping
    @Operation(
            summary = "Create a new specialization (Admin)",
            description = "Registers a new specialization in the system with all its academic information. Admin endpoint."
    )
    public ResponseEntity<SpecializationAreaResponse> createSpecialization(
            @Valid @RequestBody SpecializationAreaRequest request) {
        SpecializationAreaResponse response = specializationService.createSpecialization(request);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/specializations/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{specializationId}")
    @Operation(
            summary = "Update an existing specialization (Admin)",
            description = "Modifies the information of a specialization registered in the system. Admin endpoint."
    )
    public SpecializationAreaResponse updateSpecialization(
            @PathVariable Integer specializationId,
            @Valid @RequestBody SpecializationAreaRequest request) {
        return specializationService.updateSpecialization(specializationId, request);
    }
}