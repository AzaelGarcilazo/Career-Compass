package compass.career.CareerCompass.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

        // Detectar violaciones específicas
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

        // Detectar errores específicos
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
        // Detectar si estamos en modo desarrollo
        String env = System.getProperty("spring.profiles.active");
        return env == null || env.equals("dev") || env.equals("development");
    }
}
