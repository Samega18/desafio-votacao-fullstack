package com.cooperativa.sistema.votacao.dto;

import com.cooperativa.sistema.votacao.domain.OpcaoVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for casting a vote
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotoRequest {
    
    @NotBlank(message = "O ID do associado é obrigatório")
    private String idAssociado;
    
    @NotBlank(message = "O CPF do associado é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos")
    private String cpf;
    
    @NotNull(message = "A opção de voto é obrigatória (SIM ou NAO)")
    private OpcaoVoto opcao;
}