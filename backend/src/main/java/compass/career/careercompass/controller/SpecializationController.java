package compass.career.careercompass.controller;

import compass.career.careercompass.dto.*;
import compass.career.careercompass.model.User;
import compass.career.careercompass.service.AuthService;
import compass.career.careercompass.service.SpecializationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/specializations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SpecializationController {

    private final SpecializationService specializationService;
    private final AuthService authService;

    @PostMapping("/recommendations")
    public List<SpecializationRecommendationResponse> getRecommendedSpecializations(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return specializationService.getRecommendedSpecializations(user.getId());
    }

    @GetMapping("/details/{specializationId}")
    public SpecializationDetailResponse getSpecializationDetails(@PathVariable Integer specializationId) {
        return specializationService.getSpecializationDetails(specializationId);
    }

    @GetMapping
    public List<SpecializationAreaResponse> getAllSpecializations() {
        return specializationService.getAllSpecializations();
    }

    @GetMapping("/{specializationId}")
    public SpecializationAreaResponse getSpecializationById(@PathVariable Integer specializationId) {
        return specializationService.getSpecializationById(specializationId);
    }

    @PostMapping
    public ResponseEntity<SpecializationAreaResponse> createSpecialization(
            @Valid @RequestBody SpecializationAreaRequest request) {
        SpecializationAreaResponse response = specializationService.createSpecialization(request);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/specializations/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{specializationId}")
    public SpecializationAreaResponse updateSpecialization(
            @PathVariable Integer specializationId,
            @Valid @RequestBody SpecializationAreaRequest request) {
        return specializationService.updateSpecialization(specializationId, request);
    }
}
