package com.cooperativa.sistema.votacao.mapper;

import com.cooperativa.sistema.votacao.domain.SessaoVotacao;
import com.cooperativa.sistema.votacao.dto.SessaoVotacaoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for SessaoVotacao entity and DTOs
 */
@Mapper
public interface SessaoVotacaoMapper {
    
    /**
     * Convert entity to DTO
     */
    @Mapping(source = "pauta.id", target = "pautaId")
    @Mapping(source = "pauta.titulo", target = "tituloPauta")
    SessaoVotacaoDTO toDto(SessaoVotacao sessaoVotacao);
    
    /**
     * Convert list of entities to list of DTOs
     */
    List<SessaoVotacaoDTO> toDtoList(List<SessaoVotacao> sessoes);
}