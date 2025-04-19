package com.cooperativa.sistema.votacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for creating a new Pauta
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PautaRequest {
    
    @NotBlank(message = "O título é obrigatório")
    @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres")
    private String titulo;
    
    @Size(max = 1000, message = "A descrição não pode exceder 1000 caracteres")
    private String descricao;
}