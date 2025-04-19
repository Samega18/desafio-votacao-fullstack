package com.cooperativa.sistema.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when trying to open a voting session for an agenda that already has an active session
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class SessaoJaExistenteException extends RuntimeException {
    
    public SessaoJaExistenteException(String message) {
        super(message);
    }
    
    public SessaoJaExistenteException(String message, Throwable cause) {
        super(message, cause);
    }
}