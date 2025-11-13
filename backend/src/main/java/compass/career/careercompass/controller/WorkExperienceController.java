package compass.career.careercompass.controller;

import compass.career.careercompass.dto.WorkExperienceRequest;
import compass.career.careercompass.dto.WorkExperienceResponse;
import compass.career.careercompass.service.WorkExperienceService;
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
@RequestMapping("/api/v1/work-experience")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Work Experience", description = "Endpoints for managing user's work experience history")
public class WorkExperienceController {

    private final WorkExperienceService workExperienceService;
    private final AuthenticationHelper authHelper;

    @GetMapping(value = "pagination", params = { "page", "pageSize" })
    @Operation(
            summary = "Get work experience with pagination",
            description = "Retrieves the authenticated user's work experience history in a paginated manner."
    )
    public List<WorkExperienceResponse> getWorkExperience(
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
        return workExperienceService.findByUserId(user.getId(), page, pageSize);
    }

    @PostMapping
    @Operation(
            summary = "Add a new work experience record",
            description = "Add a new work experience record to the user's profile including company, position, and date range."
    )
    public ResponseEntity<WorkExperienceResponse> createWorkExperience(
            Authentication authentication,
            @Valid @RequestBody WorkExperienceRequest request) {
        var user = authHelper.getAuthenticatedUser(authentication);
        WorkExperienceResponse response = workExperienceService.create(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/work-experience/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a work experience by its ID",
            description = "Updates an existing work experience record, identified by its ID."
    )
    public WorkExperienceResponse updateWorkExperience(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody WorkExperienceRequest request) {
        var user = authHelper.getAuthenticatedUser(authentication);
        return workExperienceService.update(user.getId(), id, request);
    }
}