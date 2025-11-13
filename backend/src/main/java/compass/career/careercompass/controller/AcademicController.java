package compass.career.careercompass.controller;

import compass.career.careercompass.dto.AcademicInformationRequest;
import compass.career.careercompass.dto.AcademicInformationResponse;
import compass.career.careercompass.service.AcademicInformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/academic")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@Tag(name = "Academic Information", description = "Endpoints for managing user's academic history")
public class AcademicController {

    private final AcademicInformationService academicInformationService;
    private final AuthenticationHelper authHelper;

    @GetMapping(value = "pagination", params = { "page", "pageSize" })
    @Operation(
            summary = "Get paginated academic information",
            description = "Retrieves a list of academic information records belonging to the authenticated user with pagination support."
    )
    public List<AcademicInformationResponse> getAcademicInformation(
            Authentication authentication,
            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: page and pageSize cannot be negative and cannot both be 0.");
        }

        var user = authHelper.getAuthenticatedUser(authentication);
        return academicInformationService.findByUserId(user.getId(), page, pageSize);
    }

    @PostMapping
    @Operation(
            summary = "Create a new academic record",
            description = "Allows the authenticated user to add a new record to their academic history."
    )
    public ResponseEntity<AcademicInformationResponse> createAcademicInformation(
            Authentication authentication,
            @Valid @RequestBody AcademicInformationRequest request) {
        var user = authHelper.getAuthenticatedUser(authentication);
        AcademicInformationResponse response = academicInformationService.create(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/academic/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an academic record by ID",
            description = "Modifies an existing academic information record, identified by its ID."
    )
    public AcademicInformationResponse updateAcademicInformation(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody AcademicInformationRequest request) {
        var user = authHelper.getAuthenticatedUser(authentication);
        return academicInformationService.update(user.getId(), id, request);
    }
}