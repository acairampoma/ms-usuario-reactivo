package com.indra.usuarios.reactive.exception;

import com.indra.usuarios.reactive.dto.MensajeError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

// Chain of Responsibility — eslabones de error en orden de especificidad
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<MensajeError> handleEmailDuplicado(EmailDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MensajeError(ex.getMessage()));
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<MensajeError> handleValidacion(ValidacionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MensajeError(ex.getMessage()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<MensajeError> handleBeanValidation(WebExchangeBindException ex) {
        var mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("Datos de entrada inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MensajeError(mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeError> handleGeneral(Exception ex) {
        log.error("Error no controlado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MensajeError("Error interno del servidor"));
    }
}
