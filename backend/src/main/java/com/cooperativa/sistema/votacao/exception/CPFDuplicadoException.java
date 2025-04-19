package com.cooperativa.sistema.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when trying to register an associate with a CPF that already exists
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CPFDuplicadoException extends RuntimeException {
    
    public CPFDuplicadoException(String message) {
        super(message);
    }
    
    public CPFDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}