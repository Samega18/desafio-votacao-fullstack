package com.cooperativa.sistema.votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for ResultadoVotacao entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoVotacaoDTO {
    
    private Long id;
    private Long sessaoId;
    private Long pautaId;
    private String tituloPauta;
    private Integer totalVotos;
    private Integer votosSim;
    private Integer votosNao;
    private Double percentualSim;
    private Double percentualNao;
    private Boolean aprovado;
    private LocalDateTime dataApuracao;
}