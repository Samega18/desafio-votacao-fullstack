package com.cooperativa.sistema.votacao.dto;

import com.cooperativa.sistema.votacao.domain.StatusVotacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object for CPF validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CPFValidationResponse {
    
    private StatusVotacao status;
}