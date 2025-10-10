package compass.career.CareerCompass.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import compass.career.CareerCompass.dto.CareerDetailResponse;
import compass.career.CareerCompass.dto.FavoriteCareerRequest;
import compass.career.CareerCompass.dto.FavoriteCareerResponse;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.service.AuthService;
import compass.career.CareerCompass.service.CareerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CareerController.class)
class CareerControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CareerService careerService;

    @Autowired
    AuthService authService;

    private static final String BASE = "/api/v1/careers";
    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String TOKEN_WITHOUT_BEARER = "valid.jwt.token";

    @BeforeEach
    void beforeEach() {
        reset(careerService, authService);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        CareerService careerService() {
            return mock(CareerService.class);
        }

        @Bean
        AuthService authService() {
            return mock(AuthService.class);
        }
    }

    private User mockUser(Integer id, String email) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getEmail()).thenReturn(email);
        return user;
    }


    @Test
    @DisplayName("GET /{careerId} → 200 con detalles completos de la carrera")
    void getCareerDetails_ok() throws Exception {
        // Propósito: Verificar que se pueden consultar los detalles de una carrera
        // específica por su ID, obteniendo toda la información necesaria para
        // que el usuario conozca más sobre esa opción vocacional.

        CareerDetailResponse detail = mock(CareerDetailResponse.class);
        when(detail.getId()).thenReturn(5);
        when(detail.getName()).thenReturn("Ingeniería en Sistemas");
        when(detail.getDescription()).thenReturn("Carrera enfocada en desarrollo de software");

        when(careerService.getCareerDetails(5)).thenReturn(detail);

        mvc.perform(get(BASE + "/{careerId}", 5)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Ingeniería en Sistemas"));

        verify(careerService, times(1)).getCareerDetails(5);
    }

    @Test
    @DisplayName("GET /{careerId} → 404 cuando la carrera no existe")
    void getCareerDetails_notFound() throws Exception {
        // Propósito: Verificar el manejo de errores cuando se solicita una carrera
        // que no existe en el sistema, devolviendo un 404 apropiado.

        when(careerService.getCareerDetails(999))
                .thenThrow(new EntityNotFoundException("Career not found"));

        mvc.perform(get(BASE + "/{careerId}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(careerService, times(1)).getCareerDetails(999);
    }

    /*
     * ======================================================
     * POST /api/v1/careers/favorites
     * ======================================================
     */

    @Test
    @DisplayName("POST /favorites → 201 con carrera agregada a favoritos")
    void addFavoriteCareer_ok() throws Exception {
        // Propósito: Verificar que un usuario autenticado puede agregar una carrera
        // a su lista de favoritos, recibiendo confirmación con código 201 y header
        // Location. Esto permite a los usuarios marcar carreras de interés.

        User mockUser = mockUser(1, "user@example.com");
        when(authService.getUserFromToken(TOKEN_WITHOUT_BEARER)).thenReturn(mockUser);

        FavoriteCareerRequest request = mock(FavoriteCareerRequest.class);
        when(request.getCareerId()).thenReturn(10);

        FavoriteCareerResponse response = mock(FavoriteCareerResponse.class);
        when(response.getId()).thenReturn(100);
        when(response.getCareerId()).thenReturn(10);
        when(response.getCareerName()).thenReturn("Medicina");

        when(careerService.addFavoriteCareer(eq(1), any(FavoriteCareerRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                    "careerId": 10
                }
                """;

        mvc.perform(post(BASE + "/favorites")
                        .header("Authorization", VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/careers/favorites"))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.careerId").value(10))
                .andExpect(jsonPath("$.careerName").value("Medicina"));

        verify(authService, times(1)).getUserFromToken(TOKEN_WITHOUT_BEARER);
        verify(careerService, times(1)).addFavoriteCareer(eq(1), any(FavoriteCareerRequest.class));
    }

    @Test
    @DisplayName("POST /favorites → 400 con body inválido")
    void addFavoriteCareer_invalidBody() throws Exception {
        // Propósito: Verificar que el endpoint valida correctamente el body de la
        // petición y rechaza requests con datos inválidos o incompletos (400).

        User mockUser = mockUser(1, "user@example.com");
        when(authService.getUserFromToken(TOKEN_WITHOUT_BEARER)).thenReturn(mockUser);

        mvc.perform(post(BASE + "/favorites")
                        .header("Authorization", VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));

        verify(careerService, never()).addFavoriteCareer(any(), any());
    }

    /*
     * ======================================================
     * DELETE /api/v1/careers/favorites/{careerId}
     * ======================================================
     */

    @Test
    @DisplayName("DELETE /favorites/{careerId} → 204 eliminación exitosa")
    void removeFavoriteCareer_ok() throws Exception {
        // Propósito: Verificar que un usuario puede eliminar una carrera de sus
        // favoritos exitosamente, recibiendo código 204 sin body. Esto permite
        // a los usuarios gestionar su lista de carreras de interés.

        User mockUser = mockUser(1, "user@example.com");
        when(authService.getUserFromToken(TOKEN_WITHOUT_BEARER)).thenReturn(mockUser);

        doNothing().when(careerService).removeFavoriteCareer(1, 15);

        mvc.perform(delete(BASE + "/favorites/{careerId}", 15)
                        .header("Authorization", VALID_TOKEN))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(authService, times(1)).getUserFromToken(TOKEN_WITHOUT_BEARER);
        verify(careerService, times(1)).removeFavoriteCareer(1, 15);
    }

    @Test
    @DisplayName("DELETE /favorites/{careerId} → 404 cuando no existe en favoritos")
    void removeFavoriteCareer_notFound() throws Exception {
        // Propósito: Verificar el manejo de errores cuando se intenta eliminar
        // una carrera que no está en los favoritos del usuario o no existe.

        User mockUser = mockUser(1, "user@example.com");
        when(authService.getUserFromToken(TOKEN_WITHOUT_BEARER)).thenReturn(mockUser);

        doThrow(new EntityNotFoundException("Favorite career not found"))
                .when(careerService).removeFavoriteCareer(1, 999);

        mvc.perform(delete(BASE + "/favorites/{careerId}", 999)
                        .header("Authorization", VALID_TOKEN))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(careerService, times(1)).removeFavoriteCareer(1, 999);
    }

    /*
     * ======================================================
     * GET /api/v1/careers/favorites
     * ======================================================
     */

    @Test
    @DisplayName("GET /favorites → 200 con lista de carreras favoritas")
    void getFavoriteCareers_ok() throws Exception {
        // Propósito: Verificar que un usuario autenticado puede obtener su lista
        // completa de carreras favoritas, permitiéndole revisar las opciones que
        // ha marcado como de interés especial.

        User mockUser = mockUser(1, "user@example.com");
        when(authService.getUserFromToken(TOKEN_WITHOUT_BEARER)).thenReturn(mockUser);

        FavoriteCareerResponse fav1 = mock(FavoriteCareerResponse.class);
        when(fav1.getId()).thenReturn(1);
        when(fav1.getCareerId()).thenReturn(5);
        when(fav1.getCareerName()).thenReturn("Medicina");

        FavoriteCareerResponse fav2 = mock(FavoriteCareerResponse.class);
        when(fav2.getId()).thenReturn(2);
        when(fav2.getCareerId()).thenReturn(8);
        when(fav2.getCareerName()).thenReturn("Derecho");

        when(careerService.getFavoriteCareers(1)).thenReturn(List.of(fav1, fav2));

        mvc.perform(get(BASE + "/favorites")
                        .header("Authorization", VALID_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].careerName").value("Medicina"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].careerName").value("Derecho"));

        verify(authService, times(1)).getUserFromToken(TOKEN_WITHOUT_BEARER);
        verify(careerService, times(1)).getFavoriteCareers(1);
    }

    @Test
    @DisplayName("GET /favorites → 200 con lista vacía")
    void getFavoriteCareers_empty() throws Exception {
        // Propósito: Verificar que cuando un usuario no tiene carreras favoritas,
        // el sistema devuelve una lista vacía sin errores. Esto es normal para
        // usuarios nuevos o que han eliminado todos sus favoritos.

        User mockUser = mockUser(2, "newuser@example.com");
        when(authService.getUserFromToken(TOKEN_WITHOUT_BEARER)).thenReturn(mockUser);
        when(careerService.getFavoriteCareers(2)).thenReturn(Collections.emptyList());

        mvc.perform(get(BASE + "/favorites")
                        .header("Authorization", VALID_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(careerService, times(1)).getFavoriteCareers(2);
    }
}