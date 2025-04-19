package com.cooperativa.sistema.votacao.dto;

import com.cooperativa.sistema.votacao.domain.OpcaoVoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Voto entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotoDTO {
    
    private Long id;
    private String idAssociado;
    private OpcaoVoto opcao;
    private LocalDateTime dataHoraVoto;
}