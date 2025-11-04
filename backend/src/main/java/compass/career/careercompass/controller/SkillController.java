package compass.career.careercompass.controller;

import compass.career.careercompass.dto.SkillRequest;
import compass.career.careercompass.dto.SkillResponse;
import compass.career.careercompass.dto.UpdateSkillRequest;
import compass.career.careercompass.model.User;
import compass.career.careercompass.service.AuthService;
import compass.career.careercompass.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SkillController {

    private final SkillService skillService;
    private final AuthService authService;

    @GetMapping(value = "pagination", params = { "page", "pageSize" })
    @Operation(summary = "Gain skills with pagination",
    description = "Retrieves a paginated list of skills associated with the user's profile."
    )
    public List<SkillResponse> getSkills(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: page and pageSize cannot be negative and cannot both be 0.");
        }

        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return skillService.findByUserId(user.getId(), page, pageSize);
    }

    @PostMapping
    @Operation(summary = "Insert a new skill",
    description = "Allows an authenticated user to add a new skill to their profile."
    )
    public ResponseEntity<SkillResponse> createSkill(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody SkillRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        SkillResponse response = skillService.create(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/skills/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update a skill by its ID",
        description = "Modify an existing user skill, identified by its id."
    )
    public SkillResponse updateSkill(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSkillRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return skillService.update(user.getId(), id, request);
    }

}