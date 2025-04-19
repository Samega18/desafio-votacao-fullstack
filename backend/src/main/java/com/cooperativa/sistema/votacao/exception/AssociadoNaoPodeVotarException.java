package com.cooperativa.sistema.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an associate is not eligible to vote
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AssociadoNaoPodeVotarException extends RuntimeException {
    
    public AssociadoNaoPodeVotarException(String message) {
        super(message);
    }
    
    public AssociadoNaoPodeVotarException(String message, Throwable cause) {
        super(message, cause);
    }
}