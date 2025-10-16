package compass.career.CareerCompass.controller;

import compass.career.CareerCompass.dto.FavoriteCareerRequest;
import compass.career.CareerCompass.dto.FavoriteCareerResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.FavoriteCareerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/favorite-careers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Favorite Careers", description = "Endpoints para gestión de carreras favoritas del usuario")
public class FavoriteCareerController {

    private final FavoriteCareerService careerService;
    private final AuthService authService;

    @PostMapping
    @Operation(
            summary = "Agregar una carrera a favoritos",
            description = "Permite al usuario marcar una carrera como favorita para consultarla fácilmente más adelante. Se puede agregar notas personales sobre por qué la carrera es de interés. El sistema valida que no se agregue una carrera duplicada y permite un máximo de carreras favoritas por usuario."
    )
    public ResponseEntity<FavoriteCareerResponse> addFavoriteCareer(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody FavoriteCareerRequest request) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        FavoriteCareerResponse response = careerService.addFavoriteCareer(user.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/v1/careers/favorites"))
                .body(response);
    }

    @DeleteMapping("/{careerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar una carrera de favoritos",
            description = "Remueve una carrera de la lista de favoritos del usuario. La carrera se marca como inactiva pero no se elimina físicamente de la base de datos, permitiendo reactivarla posteriormente si se vuelve a agregar."
    )
    public void removeFavoriteCareer(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer careerId) {
        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        careerService.removeFavoriteCareer(user.getId(), careerId);
    }

    @GetMapping(value = "/favorites", params = { "page", "pageSize" })
    @Operation(
            summary = "Obtener carreras favoritas con paginación",
            description = "Recupera la lista de carreras marcadas como favoritas por el usuario, con soporte para paginación. Permite organizar y navegar grandes cantidades de carreras favoritas de manera eficiente. Los parámetros page y pageSize deben ser mayores o iguales a 0, y no pueden ser ambos 0 simultáneamente."
    )
    public List<FavoriteCareerResponse> getFavoriteCareers(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: page and pageSize cannot be negative and cannot both be 0.");
        }

        String cleanToken = token.replace("Bearer ", "");
        User user = authService.getUserFromToken(cleanToken);
        return careerService.getFavoriteCareers(user.getId(), page, pageSize);
    }
}