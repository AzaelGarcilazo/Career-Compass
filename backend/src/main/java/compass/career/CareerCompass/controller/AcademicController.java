package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.AcademicInformationRequest;
import compass.career.CareerCompass.dto.AcademicInformationResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AcademicInformationService;
import compass.career.CareerCompass.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/academic")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
public class AcademicController {

    private final AcademicInformationService academicInformationService;
    private final AuthService authService;

    @GetMapping(value = "pagination", params = { "page", "pageSize" })
    @Operation(summary = "Obtener información académica paginada del usuario",
     description = "Recupera una lista de los registros de información académica pertenecientes al usuario autenticado")
    public List<AcademicInformationResponse> getAcademicInformation(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: page and pageSize cannot be negative and cannot both be 0.");
        }

        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return academicInformationService.findByUserId(user.getId(), page, pageSize);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo registro académico",
    description = "Permite al usuario autenticado añadir un nuevo registro a su historial académico.")
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
    @Operation(
    summary = "Actualizar un registro académico por id",
    description = "Modifica un registro de información académica existente, identificado por su id.")
    public AcademicInformationResponse updateAcademicInformation(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @Valid @RequestBody AcademicInformationRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return academicInformationService.update(user.getId(), id, request);
    }
}
