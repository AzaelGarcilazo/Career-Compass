package compass.career.careercompass.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.SocketTimeoutException;
import java.security.SignatureException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    // ============= EXCEPCIONES DE VALIDACIÓN =============

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = buildErrorResponse(
                "VALIDATION_ERROR",
                "Validation error in the data sent",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
        body.put("fields", fieldErrors);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = buildErrorResponse(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        Map<String, Object> body = buildErrorResponse(
                "INVALID_STATE",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============= EXCEPCIONES DE BASE DE DATOS =============

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, Object> body = buildErrorResponse(
                "NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Data integrity error";
        String code = "DATA_INTEGRITY_ERROR";

        String exMessage = ex.getMessage().toLowerCase();

        if (exMessage.contains("duplicate") || exMessage.contains("unique")) {
            if (exMessage.contains("email")) {
                message = "The email is already registered in the system";
                code = "DUPLICATE_EMAIL";
            } else if (exMessage.contains("username")) {
                message = "El nombre de usuario ya está en uso";
                code = "DUPLICATE_USERNAME";
            } else {
                message = "A record with this data already exists";
                code = "DUPLICATE_ENTRY";
            }
        } else if (exMessage.contains("foreign key") || exMessage.contains("referential integrity")) {
            message = "Reference to non-existent data. Verifies that the related data exists.";
            code = "FOREIGN_KEY_VIOLATION";
        } else if (exMessage.contains("null") || exMessage.contains("not-null")) {
            message = "Mandatory data is missing from the application";
            code = "NULL_VALUE_ERROR";
        }

        Map<String, Object> body = buildErrorResponse(code, message, HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Maneja errores de conexión a la base de datos, incluyendo credenciales incorrectas
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, Object>> handleSQLException(SQLException ex) {
        String message = "Error connecting to the database";
        String code = "DATABASE_CONNECTION_ERROR";
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        String exMessage = ex.getMessage().toLowerCase();

        // Detectar credenciales incorrectas
        if (exMessage.contains("authentication failed") ||
                exMessage.contains("password authentication failed") ||
                exMessage.contains("access denied") ||
                exMessage.contains("invalid authorization")) {
            message = "Database authentication failed. Please check database credentials in configuration";
            code = "DATABASE_AUTH_FAILED";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        // Detectar que la base de datos no existe
        else if (exMessage.contains("database") && exMessage.contains("does not exist")) {
            message = "The specified database does not exist";
            code = "DATABASE_NOT_FOUND";
        }
        // Detectar timeout de conexión
        else if (exMessage.contains("timeout") || exMessage.contains("timed out")) {
            message = "Database connection timeout. The server may be unavailable";
            code = "DATABASE_TIMEOUT";
        }

        Map<String, Object> body = buildErrorResponse(code, message, status);

        if (isDevelopmentMode()) {
            body.put("sqlState", ex.getSQLState());
            body.put("errorCode", ex.getErrorCode());
            body.put("details", ex.getMessage());
        }

        return ResponseEntity.status(status).body(body);
    }

    // ============= EXCEPCIONES DE AUTENTICACIÓN Y AUTORIZACIÓN =============

    /**
     * Maneja cuando falta el header de Authorization requerido
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> handleMissingHeader(MissingRequestHeaderException ex) {
        String message = "Missing required header";
        String code = "MISSING_HEADER";

        // Detectar específicamente el header de Authorization
        if ("Authorization".equalsIgnoreCase(ex.getHeaderName())) {
            message = "Authorization header is required. Please include a valid Bearer token";
            code = "MISSING_AUTHORIZATION_HEADER";
        } else {
            message = String.format("Missing required header: %s", ex.getHeaderName());
        }

        Map<String, Object> body = buildErrorResponse(code, message, HttpStatus.UNAUTHORIZED);
        body.put("requiredHeader", ex.getHeaderName());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // ============= EXCEPCIONES DE APIS EXTERNAS =============

    /**
     * Maneja errores de cliente HTTP (4xx) al consumir APIs externas
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpClientError(HttpClientErrorException ex) {
        String message = "Error calling external API";
        String code = "EXTERNAL_API_CLIENT_ERROR";
        HttpStatus status = HttpStatus.BAD_GATEWAY;

        int statusCode = ex.getStatusCode().value();

        // Detectar errores específicos
        if (statusCode == 401) {
            message = "External API authentication failed. Please check API key or credentials";
            code = "EXTERNAL_API_UNAUTHORIZED";
        } else if (statusCode == 403) {
            message = "Access forbidden to external API. Verify API permissions and credentials";
            code = "EXTERNAL_API_FORBIDDEN";
        } else if (statusCode == 404) {
            message = "External API endpoint not found. The URL or endpoint may be incorrect";
            code = "EXTERNAL_API_NOT_FOUND";
        } else if (statusCode == 429) {
            message = "External API rate limit exceeded. Please try again later";
            code = "EXTERNAL_API_RATE_LIMIT";
            status = HttpStatus.TOO_MANY_REQUESTS;
        }

        Map<String, Object> body = buildErrorResponse(code, message, status);
        body.put("externalApiStatus", statusCode);

        if (isDevelopmentMode()) {
            body.put("externalApiResponse", ex.getResponseBodyAsString());
        }

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpServerError(HttpServerErrorException ex) {
        String message = "External API server error. The service may be temporarily unavailable";
        String code = "EXTERNAL_API_SERVER_ERROR";

        Map<String, Object> body = buildErrorResponse(code, message, HttpStatus.BAD_GATEWAY);
        body.put("externalApiStatus", ex.getStatusCode().value());

        if (isDevelopmentMode()) {
            body.put("externalApiResponse", ex.getResponseBodyAsString());
        }

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAccessException(ResourceAccessException ex) {
        String message = "Cannot connect to external service";
        String code = "EXTERNAL_SERVICE_UNAVAILABLE";

        // Detectar timeout específicamente
        if (ex.getCause() instanceof SocketTimeoutException) {
            message = "External service timeout. The connection took too long to respond. " +
                    "This may be due to network restrictions or service unavailability";
            code = "EXTERNAL_SERVICE_TIMEOUT";
        }
        // Detectar problemas de red
        else if (ex.getMessage().contains("Connection refused")) {
            message = "External service connection refused. The service may be down or unreachable";
            code = "EXTERNAL_SERVICE_CONNECTION_REFUSED";
        }
        else if (ex.getMessage().contains("Connection timed out")) {
            message = "External service connection timed out. Check network connectivity or firewall settings";
            code = "EXTERNAL_SERVICE_CONNECTION_TIMEOUT";
        }

        Map<String, Object> body = buildErrorResponse(code, message, HttpStatus.SERVICE_UNAVAILABLE);

        if (isDevelopmentMode()) {
            body.put("details", ex.getMessage());
            body.put("cause", ex.getCause() != null ? ex.getCause().getClass().getSimpleName() : "Unknown");
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleSocketTimeout(SocketTimeoutException ex) {
        String message = "Request timeout. The operation took too long to complete. " +
                "This may be due to slow network or service overload";
        String code = "REQUEST_TIMEOUT";

        Map<String, Object> body = buildErrorResponse(code, message, HttpStatus.GATEWAY_TIMEOUT);

        if (isDevelopmentMode()) {
            body.put("details", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(body);
    }

    // ============= EXCEPCIONES DE HTTP =============

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = String.format(
                "The HTTP method '%s' is not allowed for this route. Allowed methods: %s",
                ex.getMethod(),
                String.join(", ", ex.getSupportedMethods())
        );

        Map<String, Object> body = buildErrorResponse(
                "METHOD_NOT_ALLOWED",
                message,
                HttpStatus.METHOD_NOT_ALLOWED
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        String message = String.format(
                "The content type '%s' is not supported. Use 'application/json'",
                ex.getContentType()
        );

        Map<String, Object> body = buildErrorResponse(
                "UNSUPPORTED_MEDIA_TYPE",
                message,
                HttpStatus.UNSUPPORTED_MEDIA_TYPE
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {
        Map<String, Object> body = buildErrorResponse(
                "RESOURCE_NOT_FOUND",
                "The requested route does not exist on the server",
                HttpStatus.NOT_FOUND
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // ============= EXCEPCIONES DE PARÁMETROS =============

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameter(MissingServletRequestParameterException ex) {
        String message = String.format(
                "The required parameter '%s' of type %s is missing",
                ex.getParameterName(),
                ex.getParameterType()
        );

        Map<String, Object> body = buildErrorResponse(
                "MISSING_PARAMETER",
                message,
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "The parameter '%s' has an invalid value. The type %s was expected",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido"
        );

        Map<String, Object> body = buildErrorResponse(
                "INVALID_PARAMETER_TYPE",
                message,
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============= EXCEPCIONES DE PARSEO JSON =============

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "The JSON format is invalid";
        String code = "INVALID_JSON";

        String exMessage = ex.getMessage();

        if (exMessage.contains("LocalDate")) {
            message = "The date format must be YYYY-MM-DD (example: 2000-01-15)";
            code = "INVALID_DATE_FORMAT";
        } else if (exMessage.contains("LocalDateTime")) {
            message = "The date and time format must be YYYY-MM-DDTHH:mm:ss (example: 2024-01-15T10:30:00)";
            code = "INVALID_DATETIME_FORMAT";
        } else if (exMessage.contains("JSON parse error")) {
            message = "The submitted JSON contains syntax errors. Check for commas, braces, and quotes";
            code = "JSON_SYNTAX_ERROR";
        } else if (exMessage.contains("Required request body is missing")) {
            message = "A JSON body is required in the request";
            code = "MISSING_REQUEST_BODY";
        } else if (exMessage.contains("Cannot deserialize")) {
            message = "One or more fields have the wrong data type";
            code = "TYPE_MISMATCH";
        }

        Map<String, Object> body = buildErrorResponse(code, message, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============= EXCEPCIONES DE SEGURIDAD Y AUTENTICACIÓN =============

    /**
     * Maneja credenciales inválidas (email/password incorrectos)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = buildErrorResponse(
                "INVALID_CREDENTIALS",
                "The username or password is incorrect",
                HttpStatus.UNAUTHORIZED
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Maneja cuenta bloqueada o deshabilitada
     */
    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<Map<String, Object>> handleAccountStatus(AccountStatusException ex) {
        Map<String, Object> body = buildErrorResponse(
                "ACCOUNT_LOCKED",
                "The account is locked or disabled",
                HttpStatus.FORBIDDEN
        );

        if (isDevelopmentMode()) {
            body.put("details", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Maneja acceso denegado por permisos insuficientes
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> body = buildErrorResponse(
                "ACCESS_DENIED",
                "You are not authorized to access this resource",
                HttpStatus.FORBIDDEN
        );

        if (isDevelopmentMode()) {
            body.put("details", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Maneja firma JWT inválida
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJwtSignature(SignatureException ex) {
        Map<String, Object> body = buildErrorResponse(
                "INVALID_JWT_SIGNATURE",
                "The JWT signature is invalid",
                HttpStatus.FORBIDDEN
        );

        if (isDevelopmentMode()) {
            body.put("details", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Maneja JWT expirado
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwt(ExpiredJwtException ex) {
        Map<String, Object> body = buildErrorResponse(
                "JWT_EXPIRED",
                "The JWT token has expired. Please login again",
                HttpStatus.FORBIDDEN
        );

        if (isDevelopmentMode()) {
            body.put("expiredAt", ex.getClaims().getExpiration());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Maneja cualquier excepción JWT genérica no capturada
     */
    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(io.jsonwebtoken.JwtException ex) {
        String message = "Invalid JWT token";
        String code = "INVALID_JWT";

        // Detectar tipos específicos de errores JWT
        String exMessage = ex.getMessage().toLowerCase();

        if (exMessage.contains("malformed")) {
            message = "Malformed JWT token. The token format is invalid";
            code = "MALFORMED_JWT";
        } else if (exMessage.contains("unsupported")) {
            message = "Unsupported JWT token";
            code = "UNSUPPORTED_JWT";
        } else if (exMessage.contains("claims")) {
            message = "JWT claims string is empty";
            code = "EMPTY_JWT_CLAIMS";
        }

        Map<String, Object> body = buildErrorResponse(code, message, HttpStatus.FORBIDDEN);

        if (isDevelopmentMode()) {
            body.put("details", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Maneja excepciones genéricas no capturadas (fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // Log del error para debugging
        ex.printStackTrace();

        Map<String, Object> body = buildErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later",
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        if (isDevelopmentMode()) {
            body.put("details", ex.getMessage());
            body.put("exceptionType", ex.getClass().getSimpleName());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Maneja intentos de acceso sin autenticación
     * Se lanza cuando se intenta acceder a un endpoint protegido sin token o con token inválido
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        String message = "Authentication is required to access this resource";
        String code = "AUTHENTICATION_REQUIRED";

        // Detectar casos específicos
        String exMessage = ex.getMessage().toLowerCase();

        if (exMessage.contains("full authentication is required")) {
            message = "You must be logged in to access this resource. Please provide a valid authentication token";
            code = "AUTHENTICATION_REQUIRED";
        } else if (exMessage.contains("credentials")) {
            message = "Invalid authentication credentials";
            code = "INVALID_CREDENTIALS";
        }

        Map<String, Object> body = buildErrorResponse(code, message, HttpStatus.UNAUTHORIZED);

        if (isDevelopmentMode()) {
            body.put("details", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Maneja intento de acceso sin credenciales o con credenciales insuficientes
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientAuthentication(InsufficientAuthenticationException ex) {
        Map<String, Object> body = buildErrorResponse(
                "INSUFFICIENT_AUTHENTICATION",
                "Full authentication is required to access this resource. Please login first",
                HttpStatus.UNAUTHORIZED
        );

        if (isDevelopmentMode()) {
            body.put("details", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // ============= MÉTODOS AUXILIARES =============

    private Map<String, Object> buildErrorResponse(String code, String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("code", code);
        body.put("message", message);
        return body;
    }

    private boolean isDevelopmentMode() {
        String env = System.getProperty("spring.profiles.active");
        return env == null || env.equals("dev") || env.equals("development");
    }
}