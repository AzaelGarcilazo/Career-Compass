package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.WorkExperienceRequest;
import compass.career.CareerCompass.dto.WorkExperienceResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.WorkExperienceService;
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

    @GetMapping
    public List<WorkExperienceResponse> getWorkExperience(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return workExperienceService.findByUserId(user.getId());
    }

    @PostMapping
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
    public WorkExperienceResponse updateWorkExperience(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @Valid @RequestBody WorkExperienceRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return workExperienceService.update(user.getId(), id, request);
    }
}
