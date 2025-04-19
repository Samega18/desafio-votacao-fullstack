package com.cooperativa.sistema.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an associate attempts to vote more than once in the same session
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class VotoJaRealizadoException extends RuntimeException {
    
    public VotoJaRealizadoException(String message) {
        super(message);
    }
    
    public VotoJaRealizadoException(String message, Throwable cause) {
        super(message, cause);
    }
}