package com.nexusgesti.structure.adapter.in.web;

import com.nexusgesti.common.error.ApiError;
import com.nexusgesti.structure.domain.StructureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class StructureExceptionHandler {

    @ExceptionHandler(StructureException.class)
    public ResponseEntity<ApiError> handleStructure(StructureException ex) {
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
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "TRANSITION_INVALIDE", "CONFLIT_EMPLOI", "ITEM_EXISTANT" -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
