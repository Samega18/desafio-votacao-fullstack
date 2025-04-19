package com.cooperativa.sistema.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a CPF is invalid
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CPFInvalidoException extends RuntimeException {
    
    public CPFInvalidoException(String message) {
        super(message);
    }
    
    public CPFInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}