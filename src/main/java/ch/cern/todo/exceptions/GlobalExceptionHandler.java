package ch.cern.todo.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handle400BadRequest (BadRequestException clientException){
        Map<String, String> response = new HashMap<>();
        response.put("error",clientException.getError());
        response.put("message", clientException.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(DataIntegrityViolationException .class)
    public ResponseEntity<Map<String, String>> handleDataViolation (DataIntegrityViolationException dataIntegrityViolationException){
        Map<String, String> response = new HashMap<>();
        response.put("error","Database constraint violation");
        response.put("message", "Verify the entity");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedException .class)
    public ResponseEntity<Map<String, String>> handleAccessDenied (AccessDeniedException dataIntegrityViolationException){
        Map<String, String> response = new HashMap<>();
        response.put("error","Not authorized");
        response.put("message", "You are not authorized to update this profile.");

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }
}
