package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.*;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.CareerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/careers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class CareerController {

    private final CareerService careerService;
    private final AuthService authService;

    @PostMapping("/recommendations")
    public List<CareerRecommendationResponse> getRecommendedCareers(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return careerService.getRecommendedCareers(user.getId());
    }

    @GetMapping("/details/{careerId}")
    public CareerDetailResponse getCareerDetails(@PathVariable Integer careerId) {
        return careerService.getCareerDetails(careerId);
    }

    @GetMapping
    public List<CareerResponse> getAllCareers() {
        return careerService.getAllCareers();
    }

    @GetMapping("/{careerId}")
    public CareerResponse getCareerById(@PathVariable Integer careerId) {
        return careerService.getCareerById(careerId);
    }

    @PostMapping
    public ResponseEntity<CareerResponse> createCareer(@Valid @RequestBody CareerRequest request) {
        CareerResponse response = careerService.createCareer(request);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/careers/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{careerId}")
    public CareerResponse updateCareer(
            @PathVariable Integer careerId,
            @Valid @RequestBody CareerRequest request) {
        return careerService.updateCareer(careerId, request);
    }
}
