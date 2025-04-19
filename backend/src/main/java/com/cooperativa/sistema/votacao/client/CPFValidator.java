package com.cooperativa.sistema.votacao.client;

import com.cooperativa.sistema.votacao.domain.StatusVotacao;
import com.cooperativa.sistema.votacao.dto.CPFValidationResponse;
import com.cooperativa.sistema.votacao.exception.AssociadoNaoPodeVotarException;
import com.cooperativa.sistema.votacao.exception.CPFInvalidoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

/**
 * Client for validating CPF and checking if an associate can vote
 * This is a fake implementation that returns random results
 */
@Component
@Slf4j
public class CPFValidator {
    
    private final RestTemplate restTemplate;
    private final String validatorUrl;
    private final Random random = new Random();
    
    public CPFValidator(
            RestTemplate restTemplate,
            @Value("${cpf.validator.url}") String validatorUrl) {
        this.restTemplate = restTemplate;
        this.validatorUrl = validatorUrl;
    }
    
    /**
     * Validate a CPF and check if the associate can vote
     * This is a fake implementation that returns random results
     * 
     * @param cpf CPF to validate
     * @return Status of the validation (ABLE_TO_VOTE or UNABLE_TO_VOTE)
     * @throws CPFInvalidoException if the CPF is invalid
     * @throws AssociadoNaoPodeVotarException if the associate cannot vote
     */
    public StatusVotacao validarCPF(String cpf) {
        log.info("Validando CPF: {}", cpf);
        
        // Simulate a fake CPF validation service
        try {
            // 20% chance of the CPF being invalid
            if (random.nextInt(5) == 0) {
                log.info("CPF inválido: {}", cpf);
                throw new CPFInvalidoException("CPF inválido");
            }
            
            // 10% chance of the associate not being able to vote
            if (random.nextInt(10) == 0) {
                log.info("Associado não pode votar: {}", cpf);
                throw new AssociadoNaoPodeVotarException("Associado não pode votar");
            }
            
            // In a real scenario, we would make an HTTP request to the CPF validator service
            // ResponseEntity<CPFValidationResponse> response = 
            //     restTemplate.getForEntity(validatorUrl + "?cpf=" + cpf, CPFValidationResponse.class);
            
            // For this fake implementation, we return ABLE_TO_VOTE
            log.info("CPF válido e associado pode votar: {}", cpf);
            return StatusVotacao.ABLE_TO_VOTE;
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CPFInvalidoException("CPF inválido");
            }
            throw e;
        }
    }
}