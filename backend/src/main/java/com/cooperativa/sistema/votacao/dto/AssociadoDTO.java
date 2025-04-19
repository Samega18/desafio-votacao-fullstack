package com.cooperativa.sistema.votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Associado entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociadoDTO {
    
    private String id;
    private String nome;
    private String cpf;
    private Boolean ativo;
}