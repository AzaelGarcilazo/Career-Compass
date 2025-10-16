package compass.career.CareerCompass.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import compass.career.CareerCompass.dto.*;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CareerController.class)
@DisplayName("CareerController Tests")
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

    // =============== POST /recommendations ===============

    @Test
    @DisplayName("POST /recommendations → 200 con lista de recomendaciones generadas por IA")
    void getRecommendedCareers_success() throws Exception {
        // Propósito: Verificar que un usuario autenticado puede obtener recomendaciones
        // de carreras personalizadas basadas en sus evaluaciones completadas

        User mockUser = mockUser(1, "test@example.com");
        when(authService.getUserFromToken(TOKEN_WITHOUT_BEARER)).thenReturn(mockUser);

        List<CareerRecommendationResponse> recommendations = Arrays.asList(
                CareerRecommendationResponse.builder()
                        .id(1)
                        .name("Ingeniería en Sistemas")
                        .description("Carrera tecnológica")
                        .compatibilityPercentage(BigDecimal.valueOf(94.5))
                        .build(),
                CareerRecommendationResponse.builder()
                        .id(2)
                        .name("Medicina")
                        .description("Ciencias de la salud")
                        .compatibilityPercentage(BigDecimal.valueOf(88.2))
                        .build()
        );

        when(careerService.getRecommendedCareers(1)).thenReturn(recommendations);

        mvc.perform(post(BASE + "/recommendations")
                        .header("Authorization", VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ingeniería en Sistemas"))
                .andExpect(jsonPath("$[0].compatibilityPercentage").value(94.5))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Medicina"))
                .andExpect(jsonPath("$[1].compatibilityPercentage").value(88.2));

        verify(authService, times(1)).getUserFromToken(TOKEN_WITHOUT_BEARER);
        verify(careerService, times(1)).getRecommendedCareers(1);
    }

    @Test
    @DisplayName("POST /recommendations → 400 cuando el usuario no ha completado evaluaciones")
    void getRecommendedCareers_noEvaluations() throws Exception {
        // Propósito: Verificar que se retorna error cuando el usuario solicita
        // recomendaciones sin haber completado ninguna evaluación

        User mockUser = mockUser(1, "test@example.com");
        when(authService.getUserFromToken(TOKEN_WITHOUT_BEARER)).thenReturn(mockUser);

        when(careerService.getRecommendedCareers(1))
                .thenThrow(new IllegalStateException("User must complete at least one evaluation to get recommendations"));

        mvc.perform(post(BASE + "/recommendations")
                        .header("Authorization", VALID_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_STATE"))
                .andExpect(jsonPath("$.message").value("User must complete at least one evaluation to get recommendations"));

        verify(careerService, times(1)).getRecommendedCareers(1);
    }

    @Test
    @DisplayName("POST /recommendations → 200 con lista vacía cuando no hay carreras disponibles")
    void getRecommendedCareers_noCareersAvailable() throws Exception {
        // Propósito: Verificar el manejo cuando no existen carreras en el sistema

        User mockUser = mockUser(1, "test@example.com");
        when(authService.getUserFromToken(TOKEN_WITHOUT_BEARER)).thenReturn(mockUser);

        when(careerService.getRecommendedCareers(1))
                .thenThrow(new IllegalStateException("No careers available in the system"));

        mvc.perform(post(BASE + "/recommendations")
                        .header("Authorization", VALID_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No careers available in the system"));

        verify(careerService, times(1)).getRecommendedCareers(1);
    }

    // =============== GET /details/{careerId} ===============

    @Test
    @DisplayName("GET /details/{careerId} → 200 con detalles completos de la carrera")
    void getCareerDetails_success() throws Exception {
        // Propósito: Verificar que se pueden obtener detalles extendidos de una carrera,
        // incluyendo información de redes sociales

        CareerDetailResponse detail = CareerDetailResponse.builder()
                .id(5)
                .name("Ingeniería en Sistemas")
                .description("Carrera enfocada en desarrollo de software")
                .durationSemesters(9)
                .averageSalary(BigDecimal.valueOf(50000))
                .graduateProfile("Programación, Lógica, Matemáticas")
                .jobField("Desarrollador, Analista, Arquitecto")
                .socialMediaData(Collections.singletonMap("reddit_posts", 150))
                .build();

        when(careerService.getCareerDetails(5)).thenReturn(detail);

        mvc.perform(get(BASE + "/details/{careerId}", 5)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Ingeniería en Sistemas"))
                .andExpect(jsonPath("$.description").value("Carrera enfocada en desarrollo de software"))
                .andExpect(jsonPath("$.durationSemesters").value(9))
                .andExpect(jsonPath("$.averageSalary").value(50000))
                .andExpect(jsonPath("$.socialMediaData.reddit_posts").value(150));

        verify(careerService, times(1)).getCareerDetails(5);
    }

    @Test
    @DisplayName("GET /details/{careerId} → 404 cuando la carrera no existe")
    void getCareerDetails_notFound() throws Exception {
        // Propósito: Verificar manejo de error cuando se solicita una carrera inexistente

        when(careerService.getCareerDetails(999))
                .thenThrow(new EntityNotFoundException("Career not found"));

        mvc.perform(get(BASE + "/details/{careerId}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        verify(careerService, times(1)).getCareerDetails(999);
    }

    // =============== GET / (getAllCareers) ===============

    @Test
    @DisplayName("GET / → 200 con lista de todas las carreras disponibles")
    void getAllCareers_success() throws Exception {
        // Propósito: Verificar que se pueden listar todas las carreras del sistema

        List<CareerResponse> careers = Arrays.asList(
                CareerResponse.builder()
                        .id(1)
                        .name("Medicina")
                        .description("Ciencias de la salud")
                        .durationSemesters(12)
                        .build(),
                CareerResponse.builder()
                        .id(2)
                        .name("Derecho")
                        .description("Ciencias jurídicas")
                        .durationSemesters(10)
                        .build(),
                CareerResponse.builder()
                        .id(3)
                        .name("Ingeniería Civil")
                        .description("Construcción e infraestructura")
                        .durationSemesters(10)
                        .build()
        );

        when(careerService.getAllCareers()).thenReturn(careers);

        mvc.perform(get(BASE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Medicina"))
                .andExpect(jsonPath("$[1].name").value("Derecho"))
                .andExpect(jsonPath("$[2].name").value("Ingeniería Civil"));

        verify(careerService, times(1)).getAllCareers();
    }

    @Test
    @DisplayName("GET / → 400 cuando no hay carreras en el sistema")
    void getAllCareers_empty() throws Exception {
        // Propósito: Verificar manejo cuando el sistema no tiene carreras registradas

        when(careerService.getAllCareers())
                .thenThrow(new IllegalArgumentException("No hay carreras disponibles en el sistema"));

        mvc.perform(get(BASE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.message").value("No hay carreras disponibles en el sistema"));

        verify(careerService, times(1)).getAllCareers();
    }

    // =============== GET /{careerId} ===============

    @Test
    @DisplayName("GET /{careerId} → 200 con información básica de la carrera")
    void getCareerById_success() throws Exception {
        // Propósito: Verificar que se puede obtener información básica de una carrera por su ID

        CareerResponse career = CareerResponse.builder()
                .id(7)
                .name("Arquitectura")
                .description("Diseño y construcción")
                .durationSemesters(10)
                .averageSalary(BigDecimal.valueOf(45000))
                .build();

        when(careerService.getCareerById(7)).thenReturn(career);

        mvc.perform(get(BASE + "/{careerId}", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Arquitectura"))
                .andExpect(jsonPath("$.description").value("Diseño y construcción"))
                .andExpect(jsonPath("$.durationSemesters").value(10))
                .andExpect(jsonPath("$.averageSalary").value(45000));

        verify(careerService, times(1)).getCareerById(7);
    }

    @Test
    @DisplayName("GET /{careerId} → 404 cuando la carrera no existe")
    void getCareerById_notFound() throws Exception {
        // Propósito: Verificar manejo de error al buscar una carrera inexistente

        when(careerService.getCareerById(999))
                .thenThrow(new EntityNotFoundException("The requested career has not been found"));

        mvc.perform(get(BASE + "/{careerId}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("The requested career has not been found"));

        verify(careerService, times(1)).getCareerById(999);
    }

    // =============== POST / (createCareer) ===============

    @Test
    @DisplayName("POST / → 201 con carrera creada exitosamente")
    void createCareer_success() throws Exception {
        // Propósito: Verificar que un administrador puede crear una nueva carrera

        CareerRequest request = CareerRequest.builder()
                .name("Ingeniería Biomédica")
                .description("Aplicación de ingeniería a medicina")
                .durationSemesters(9)
                .averageSalary(BigDecimal.valueOf(55000))
                .graduateProfile("Matemáticas, Biología, Electrónica")
                .jobField("Hospitales, Industria médica")
                .build();

        CareerResponse response = CareerResponse.builder()
                .id(100)
                .name("Ingeniería Biomédica")
                .description("Aplicación de ingeniería a medicina")
                .durationSemesters(9)
                .averageSalary(BigDecimal.valueOf(55000))
                .build();

        when(careerService.createCareer(any(CareerRequest.class))).thenReturn(response);

        String requestBody = mapper.writeValueAsString(request);

        mvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/admin/careers/100"))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Ingeniería Biomédica"))
                .andExpect(jsonPath("$.durationSemesters").value(9));

        verify(careerService, times(1)).createCareer(any(CareerRequest.class));
    }

    @Test
    @DisplayName("POST / → 422 con datos de carrera inválidos")
    void createCareer_invalidData() throws Exception {
        // Propósito: Verificar validación de datos al crear una carrera

        String invalidRequest = """
                {
                    "name": "",
                    "description": "Test",
                    "durationSemesters": -5
                }
                """;

        mvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(careerService, never()).createCareer(any());
    }

    // =============== PUT /{careerId} ===============

    @Test
    @DisplayName("PUT /{careerId} → 200 con carrera actualizada")
    void updateCareer_success() throws Exception {
        // Propósito: Verificar que se puede actualizar una carrera existente

        CareerRequest request = CareerRequest.builder()
                .name("Medicina Actualizada")
                .description("Nueva descripción")
                .durationSemesters(12)
                .averageSalary(BigDecimal.valueOf(70000))
                .build();

        CareerResponse response = CareerResponse.builder()
                .id(5)
                .name("Medicina Actualizada")
                .description("Nueva descripción")
                .durationSemesters(12)
                .averageSalary(BigDecimal.valueOf(70000))
                .build();

        when(careerService.updateCareer(eq(5), any(CareerRequest.class))).thenReturn(response);

        String requestBody = mapper.writeValueAsString(request);

        mvc.perform(put(BASE + "/{careerId}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Medicina Actualizada"))
                .andExpect(jsonPath("$.averageSalary").value(70000));

        verify(careerService, times(1)).updateCareer(eq(5), any(CareerRequest.class));
    }

    @Test
    @DisplayName("PUT /{careerId} → 404 cuando la carrera a actualizar no existe")
    void updateCareer_notFound() throws Exception {
        // Propósito: Verificar manejo de error al intentar actualizar una carrera inexistente

        CareerRequest request = CareerRequest.builder()
                .name("Test")
                .description("Test")
                .durationSemesters(8)
                .build();

        when(careerService.updateCareer(eq(999), any(CareerRequest.class)))
                .thenThrow(new EntityNotFoundException("The career to update has not been found"));

        String requestBody = mapper.writeValueAsString(request);

        mvc.perform(put(BASE + "/{careerId}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("The career to update has not been found"));

        verify(careerService, times(1)).updateCareer(eq(999), any(CareerRequest.class));
    }

    @Test
    @DisplayName("PUT /{careerId} → 422 con datos inválidos en actualización")
    void updateCareer_invalidData() throws Exception {
        // Propósito: Verificar validación de datos al actualizar

        String invalidRequest = """
                {
                    "name": "",
                    "durationSemesters": 0
                }
                """;

        mvc.perform(put(BASE + "/{careerId}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(careerService, never()).updateCareer(any(), any());
    }
}