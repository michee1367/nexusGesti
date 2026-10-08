package com.nexusgesti.tenant.adapter.in.web;

import com.nexusgesti.common.error.ApiError;
import com.nexusgesti.tenant.domain.TenantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class TenantExceptionHandler {

    @ExceptionHandler(TenantException.class)
    public ResponseEntity<ApiError> handleTenant(TenantException ex) {
        HttpStatus status = mapStatus(ex.getCode());
        return ResponseEntity.status(status).body(new ApiError(
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
            case "TENANT_ALREADY_EXISTS", "GRILLE_FIGEE", "ITEM_EXISTANT" -> HttpStatus.CONFLICT;
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "TRANSITION_INVALIDE" -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}