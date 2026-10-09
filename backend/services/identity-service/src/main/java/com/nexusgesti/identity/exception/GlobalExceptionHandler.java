package com.nexusgesti.identity.exception;

import com.nexusgesti.common.error.ApiError;
import com.nexusgesti.identity.domain.IdentityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdentityException.class)
    public ResponseEntity<ApiError> handleIdentity(IdentityException ex) {
        return ResponseEntity.status(mapStatus(ex.getCode())).body(new ApiError(
                ex.getCode(), ex.getMessage(), UUID.randomUUID(), Instant.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .orElse("Requete invalide");
        return ResponseEntity.badRequest().body(new ApiError(
                "INVALIDE", message, UUID.randomUUID(), Instant.now()));
    }

    private HttpStatus mapStatus(String code) {
        return switch (code) {
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "UTILISATEUR_EXISTANT", "TRANSITION_INVALIDE", "ROLE_EXISTANT", "PERMISSION_EXISTANTE",
                 "ROLE_INCONNU", "ROLE_INVALIDE", "KEYCLOAK_CONFLIT", "MOTIF_REQUIS", "CODE_REQUIS" ->
                    HttpStatus.CONFLICT;
            case "KEYCLOAK_INDISPONIBLE" -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
