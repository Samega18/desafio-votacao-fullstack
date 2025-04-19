package com.cooperativa.sistema.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when trying to vote in a closed session
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class SessaoEncerradaException extends RuntimeException {
    
    public SessaoEncerradaException(String message) {
        super(message);
    }
    
    public SessaoEncerradaException(String message, Throwable cause) {
        super(message, cause);
    }
}