package compass.career.careercompass.controller;

import compass.career.careercompass.dto.SkillRequest;
import compass.career.careercompass.dto.SkillResponse;
import compass.career.careercompass.dto.UpdateSkillRequest;
import compass.career.careercompass.service.SkillService;
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
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Skills", description = "Endpoints for managing user's technical and professional skills")
public class SkillController {

    private final SkillService skillService;
    private final AuthenticationHelper authHelper;

    @GetMapping(value = "pagination", params = { "page", "pageSize" })
    @Operation(
            summary = "Get skills with pagination",
            description = "Retrieves a paginated list of skills associated with the user's profile."
    )
    public List<SkillResponse> getSkills(
            Authentication authentication,
            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @Parameter(description = "Number of skills per page", example = "10")
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: page and pageSize cannot be negative and cannot both be 0.");
        }

        var user = authHelper.getAuthenticatedUser(authentication);
        return skillService.findByUserId(user.getId(), page, pageSize);
    }

    @PostMapping
    @Operation(
            summary = "Insert a new skill",
            description = "Allows an authenticated user to add a new skill to their profile with a proficiency level."
    )
    public ResponseEntity<SkillResponse> createSkill(
            Authentication authentication,
            @Valid @RequestBody SkillRequest request) {
        var user = authHelper.getAuthenticatedUser(authentication);
        SkillResponse response = skillService.create(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/skills/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a skill by its ID",
            description = "Modify an existing user skill, identified by its ID. Can update skill name and proficiency level."
    )
    public SkillResponse updateSkill(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSkillRequest request) {
        var user = authHelper.getAuthenticatedUser(authentication);
        return skillService.update(user.getId(), id, request);
    }
}