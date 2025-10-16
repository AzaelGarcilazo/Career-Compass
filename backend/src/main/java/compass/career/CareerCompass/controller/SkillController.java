package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.SkillRequest;
import compass.career.CareerCompass.dto.SkillResponse;
import compass.career.CareerCompass.dto.UpdateSkillRequest;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping(value = "pagination", params = { "page", "pageSize" })
    @Operation(summary = "Obtener habilidades con paginación",
    description = "Recupera una lista paginada de las habilidades asociadas al perfil del usuario."
    )
    public List<SkillResponse> getSkills(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: page and pageSize cannot be negative and cannot both be 0.");
        }

        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return skillService.findByUserId(user.getId(), page, pageSize);
    }

    @PostMapping
    @Operation(summary = "Insertar una nueva habilidad",
    description = "Permite a un usuario autenticado añadir una nueva habilidad a su perfil."
    )
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
    @Operation(
        summary = "Actualizar una habilidad por su id",
        description = "Modifica una habilidad existente del usuario, identificada por su id."
    )
    public SkillResponse updateSkill(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSkillRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return skillService.update(user.getId(), id, request);
    }

}