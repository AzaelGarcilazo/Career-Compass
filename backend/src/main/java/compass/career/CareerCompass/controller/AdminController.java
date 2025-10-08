package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.*;
import compass.career.CareerCompass.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AdminController {

    private final AdminService adminService;

    // =============== TESTS ===============

    @GetMapping("/tests")
    public List<TestListResponse> getAllTests() {
        return adminService.getAllTests();
    }

    @GetMapping("/tests/{testId}")
    public TestResponse getTestDetails(@PathVariable Integer testId) {
        return adminService.getTestDetails(testId);
    }

    @PostMapping("/tests")
    public ResponseEntity<TestResponse> createTest(@Valid @RequestBody CreateTestRequest request) {
        TestResponse response = adminService.createTest(request);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/tests/" + response.getId()))
                .body(response);
    }

    @PutMapping("/tests/{testId}")
    public TestResponse updateTest(
            @PathVariable Integer testId,
            @Valid @RequestBody CreateTestRequest request) {
        return adminService.updateTest(testId, request);
    }

    // =============== CAREERS ===============

    @GetMapping("/careers")
    public List<CareerResponse> getAllCareers() {
        return adminService.getAllCareers();
    }

    @GetMapping("/careers/{careerId}")
    public CareerResponse getCareerDetails(@PathVariable Integer careerId) {
        return adminService.getCareerDetails(careerId);
    }

    @PostMapping("/careers")
    public ResponseEntity<CareerResponse> createCareer(@Valid @RequestBody CareerRequest request) {
        CareerResponse response = adminService.createCareer(request);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/careers/" + response.getId()))
                .body(response);
    }

    @PutMapping("/careers/{careerId}")
    public CareerResponse updateCareer(
            @PathVariable Integer careerId,
            @Valid @RequestBody CareerRequest request) {
        return adminService.updateCareer(careerId, request);
    }

    // =============== SPECIALIZATIONS ===============

    @GetMapping("/specializations")
    public List<SpecializationAreaResponse> getAllSpecializations() {
        return adminService.getAllSpecializations();
    }

    @GetMapping("/specializations/{specializationId}")
    public SpecializationAreaResponse getSpecializationDetails(@PathVariable Integer specializationId) {
        return adminService.getSpecializationDetails(specializationId);
    }

    @PostMapping("/specializations")
    public ResponseEntity<SpecializationAreaResponse> createSpecialization(
            @Valid @RequestBody SpecializationAreaRequest request) {
        SpecializationAreaResponse response = adminService.createSpecialization(request);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/specializations/" + response.getId()))
                .body(response);
    }

    @PutMapping("/specializations/{specializationId}")
    public SpecializationAreaResponse updateSpecialization(
            @PathVariable Integer specializationId,
            @Valid @RequestBody SpecializationAreaRequest request) {
        return adminService.updateSpecialization(specializationId, request);
    }
}
