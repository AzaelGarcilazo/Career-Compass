package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.*;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.CareerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/careers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Careers", description = "Endpoints para gestión de carreras universitarias")
public class CareerController {

    private final CareerService careerService;
    private final AuthService authService;

    @PostMapping("/recommendations")
    @Operation(
            summary = "Obtener recomendaciones de carreras personalizadas",
            description = "Genera o recupera recomendaciones de carreras basadas en los resultados de las evaluaciones del usuario (personalidad, intereses vocacionales y habilidades cognitivas). Utiliza IA de Groq para generar recomendaciones personalizadas la primera vez. Las recomendaciones se almacenan en caché por 1 hora y en base de datos de forma permanente."
    )
    public List<CareerRecommendationResponse> getRecommendedCareers(
            @RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return careerService.getRecommendedCareers(user.getId());
    }

    @GetMapping("/details/{careerId}")
    @Operation(
            summary = "Obtener detalles completos de una carrera",
            description = "Recupera información detallada de una carrera específica, incluyendo descripción, duración, salario promedio, habilidades requeridas, oportunidades laborales y datos de redes sociales (Reddit) sobre la carrera."
    )
    public CareerDetailResponse getCareerDetails(@PathVariable Integer careerId) {
        return careerService.getCareerDetails(careerId);
    }

    @GetMapping
    @Operation(
            summary = "Listar todas las carreras disponibles",
            description = "Obtiene el catálogo completo de carreras universitarias disponibles en el sistema, con información básica de cada una."
    )
    public List<CareerResponse> getAllCareers() {
        return careerService.getAllCareers();
    }

    @GetMapping("/{careerId}")
    @Operation(
            summary = "Obtener información básica de una carrera por ID",
            description = "Recupera los datos básicos de una carrera específica utilizando su identificador único."
    )
    public CareerResponse getCareerById(@PathVariable Integer careerId) {
        return careerService.getCareerById(careerId);
    }

    @PostMapping
    @Operation(
            summary = "Crear una nueva carrera (Admin)",
            description = "Registra una nueva carrera en el sistema con toda su información académica y laboral. Endpoint de administración."
    )
    public ResponseEntity<CareerResponse> createCareer(@Valid @RequestBody CareerRequest request) {
        CareerResponse response = careerService.createCareer(request);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/careers/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{careerId}")
    @Operation(
            summary = "Actualizar una carrera existente (Admin)",
            description = "Modifica la información de una carrera registrada en el sistema. Endpoint de administración."
    )
    public CareerResponse updateCareer(
            @PathVariable Integer careerId,
            @Valid @RequestBody CareerRequest request) {
        return careerService.updateCareer(careerId, request);
    }
}