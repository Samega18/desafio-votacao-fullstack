package com.cooperativa.sistema.votacao.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the API
 */
@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    /**
     * Error response structure
     */
    private static class ErrorResponse {
        private LocalDateTime timestamp;
        private Integer status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> fieldErrors;

        public ErrorResponse(HttpStatus status, String message, String path) {
            this.timestamp = LocalDateTime.now();
            this.status = status.value();
            this.error = status.getReasonPhrase();
            this.message = message;
            this.path = path;
        }

        public void setFieldErrors(Map<String, String> fieldErrors) {
            this.fieldErrors = fieldErrors;
        }

        // Getters
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public Integer getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public String getPath() {
            return path;
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                request.getDescription(false).substring(4));
        response.setFieldErrors(errors);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.error("Recurso não encontrado: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false).substring(4));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle SessaoJaExistenteException
     */
    @ExceptionHandler(SessaoJaExistenteException.class)
    public ResponseEntity<ErrorResponse> handleSessaoJaExistenteException(
            SessaoJaExistenteException ex, WebRequest request) {
        
        log.error("Sessão já existente: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getDescription(false).substring(4));
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle SessaoEncerradaException
     */
    @ExceptionHandler(SessaoEncerradaException.class)
    public ResponseEntity<ErrorResponse> handleSessaoEncerradaException(
            SessaoEncerradaException ex, WebRequest request) {
        
        log.error("Sessão encerrada: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getDescription(false).substring(4));
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle VotoJaRealizadoException
     */
    @ExceptionHandler(VotoJaRealizadoException.class)
    public ResponseEntity<ErrorResponse> handleVotoJaRealizadoException(
            VotoJaRealizadoException ex, WebRequest request) {
        
        log.error("Voto já realizado: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getDescription(false).substring(4));
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle CPFDuplicadoException
     */
    @ExceptionHandler(CPFDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleCPFDuplicadoException(
            CPFDuplicadoException ex, WebRequest request) {
        
        log.error("CPF duplicado: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getDescription(false).substring(4));
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle CPFInvalidoException
     */
    @ExceptionHandler(CPFInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleCPFInvalidoException(
            CPFInvalidoException ex, WebRequest request) {
        
        log.error("CPF inválido: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false).substring(4));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle AssociadoNaoPodeVotarException
     */
    @ExceptionHandler(AssociadoNaoPodeVotarException.class)
    public ResponseEntity<ErrorResponse> handleAssociadoNaoPodeVotarException(
            AssociadoNaoPodeVotarException ex, WebRequest request) {
        
        log.error("Associado não pode votar: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getDescription(false).substring(4));
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex, WebRequest request) {
        
        log.error("Erro não tratado: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno no servidor",
                request.getDescription(false).substring(4));
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}