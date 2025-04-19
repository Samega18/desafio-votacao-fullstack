package com.cooperativa.sistema.votacao.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for opening a voting session
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoRequest {
    
    @Min(value = 1, message = "A duração mínima da sessão deve ser de 1 minuto")
    private Integer duracaoMinutos;
}