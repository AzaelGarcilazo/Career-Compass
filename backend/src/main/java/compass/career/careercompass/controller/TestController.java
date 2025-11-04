package compass.career.careercompass.controller;

import compass.career.careercompass.dto.CreateTestRequest;
import compass.career.careercompass.dto.TestListResponse;
import compass.career.careercompass.dto.TestResponse;
import compass.career.careercompass.service.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class TestController {

    private final TestService testService;

    // =============== ENDPOINTS PÚBLICOS/USUARIO ===============

    @GetMapping
    public List<TestListResponse> getAllTests() {
        return testService.getAllTests();
    }

    @GetMapping("/{testId}")
    public TestResponse getTestDetails(@PathVariable Integer testId) {
        return testService.getTestDetails(testId);
    }

    // =============== ENDPOINTS DE ADMINISTRACIÓN ===============

    @PostMapping
    public ResponseEntity<TestResponse> createTest(@Valid @RequestBody CreateTestRequest request) {
        TestResponse response = testService.createTest(request);
        return ResponseEntity
                .created(URI.create("/api/v1/tests/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{testId}")
    public TestResponse updateTest(
            @PathVariable Integer testId,
            @Valid @RequestBody CreateTestRequest request) {
        return testService.updateTest(testId, request);
    }
}