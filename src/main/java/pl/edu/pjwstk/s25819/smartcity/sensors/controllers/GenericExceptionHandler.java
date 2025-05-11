package pl.edu.pjwstk.s25819.smartcity.sensors.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.edu.pjwstk.s25819.smartcity.sensors.dto.GenericErrorResponseDto;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        GenericErrorResponseDto errorResponse = new GenericErrorResponseDto(
                errors.toString(),
                "Validation Error",
                ex.getParameter().getExecutable().toString(), // lub dodaj własny sposób
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericErrorResponseDto> handleGenericException(Exception exception, HttpServletRequest request) {
        GenericErrorResponseDto errorResponse = new GenericErrorResponseDto(
                exception.getMessage(),
                exception.getClass().getSimpleName(),
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                System.currentTimeMillis()
        );

        return ResponseEntity.internalServerError().body(errorResponse);
    }

    private String getPrintStackTrace(Exception exception) {

        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
