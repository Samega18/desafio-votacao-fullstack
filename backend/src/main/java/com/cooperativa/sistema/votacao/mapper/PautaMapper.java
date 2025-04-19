package com.cooperativa.sistema.votacao.mapper;

import com.cooperativa.sistema.votacao.domain.Pauta;
import com.cooperativa.sistema.votacao.dto.PautaDTO;
import com.cooperativa.sistema.votacao.dto.PautaRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for Pauta entity and DTOs
 */
@Mapper
public interface PautaMapper {
    
    /**
     * Convert entity to DTO
     */
    PautaDTO toDto(Pauta pauta);
    
    /**
     * Convert request to entity
     */
    Pauta toEntity(PautaRequest request);
    
    /**
     * Convert list of entities to list of DTOs
     */
    List<PautaDTO> toDtoList(List<Pauta> pautas);
}