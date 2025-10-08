package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.AcademicInformationRequest;
import compass.career.CareerCompass.dto.AcademicInformationResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AcademicInformationService;
import compass.career.CareerCompass.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/academic")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AcademicController {

    private final AcademicInformationService academicInformationService;
    private final AuthService authService;

    @GetMapping
    public List<AcademicInformationResponse> getAcademicInformation(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return academicInformationService.findByUserId(user.getId());
    }

    @PostMapping
    public ResponseEntity<AcademicInformationResponse> createAcademicInformation(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AcademicInformationRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        AcademicInformationResponse response = academicInformationService.create(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/academic/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    public AcademicInformationResponse updateAcademicInformation(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @Valid @RequestBody AcademicInformationRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return academicInformationService.update(user.getId(), id, request);
    }
}
