package com.cooperativa.sistema.votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for SessaoVotacao entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoVotacaoDTO {
    
    private Long id;
    private Long pautaId;
    private String tituloPauta;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private Boolean encerrada;
}