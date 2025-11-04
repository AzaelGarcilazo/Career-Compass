package compass.career.careercompass.controller;

import compass.career.careercompass.dto.WorkExperienceRequest;
import compass.career.careercompass.dto.WorkExperienceResponse;
import compass.career.careercompass.model.User;
import compass.career.careercompass.service.AuthService;
import compass.career.careercompass.service.WorkExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/work-experience")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class WorkExperienceController {

    private final WorkExperienceService workExperienceService;
    private final AuthService authService;

    @GetMapping(value = "pagination", params = { "page", "pageSize" })
    @Operation(summary = "Gain work experience with pagination",
    description = "Retrieves the authenticated user's work experience history in a paginated manner."
    )
    public List<WorkExperienceResponse> getWorkExperience(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: page and pageSize cannot be negative and cannot both be 0.");
        }

        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return workExperienceService.findByUserId(user.getId(), page, pageSize);
    }

    @PostMapping
    @Operation(
        summary = "Add a new work experience record",
        description = "Add a new work experience record to the user's profile."
    )
    public ResponseEntity<WorkExperienceResponse> createWorkExperience(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody WorkExperienceRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        WorkExperienceResponse response = workExperienceService.create(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/work-experience/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a work experience by its ID",
    description = "Updates an existing work experience record, identified by its ID."
    )
    public WorkExperienceResponse updateWorkExperience(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @Valid @RequestBody WorkExperienceRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return workExperienceService.update(user.getId(), id, request);
    }
}
