package com.cooperativa.sistema.votacao.mapper;

import com.cooperativa.sistema.votacao.domain.ResultadoVotacao;
import com.cooperativa.sistema.votacao.dto.ResultadoVotacaoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for ResultadoVotacao entity and DTOs
 */
@Mapper
public interface ResultadoVotacaoMapper {
    
    /**
     * Convert entity to DTO
     */
    @Mapping(source = "sessaoVotacao.id", target = "sessaoId")
    @Mapping(source = "sessaoVotacao.pauta.id", target = "pautaId")
    @Mapping(source = "sessaoVotacao.pauta.titulo", target = "tituloPauta")
    @Mapping(source = "percentualSim", target = "percentualSim")
    @Mapping(source = "percentualNao", target = "percentualNao")
    @Mapping(source = "aprovado", target = "aprovado")
    ResultadoVotacaoDTO toDto(ResultadoVotacao resultadoVotacao);
}