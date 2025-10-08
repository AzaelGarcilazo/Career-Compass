package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.SkillRequest;
import compass.career.CareerCompass.dto.SkillResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SkillController {

    private final SkillService skillService;
    private final AuthService authService;

    @GetMapping
    public List<SkillResponse> getSkills(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return skillService.findByUserId(user.getId());
    }

    @PostMapping
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

    @PatchMapping("/{id}/proficiency")
    public SkillResponse updateProficiencyLevel(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        Integer newLevel = body.get("proficiencyLevel");
        if (newLevel == null) {
            throw new IllegalArgumentException("proficiencyLevel is required");
        }
        return skillService.updateProficiencyLevel(user.getId(), id, newLevel);
    }
}
