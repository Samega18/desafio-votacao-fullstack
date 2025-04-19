package com.cooperativa.sistema.votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Pauta entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PautaDTO {
    
    private Long id;
    private String titulo;
    private String descricao;
    private LocalDateTime dataCriacao;
}