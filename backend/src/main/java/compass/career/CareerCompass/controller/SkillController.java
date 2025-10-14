package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.SkillRequest;
import compass.career.CareerCompass.dto.SkillResponse;
import compass.career.CareerCompass.dto.UpdateSkillRequest;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.SkillService;
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

    @PutMapping("/{id}")
    public SkillResponse updateSkill(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSkillRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return skillService.update(user.getId(), id, request);
    }

}