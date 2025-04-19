package com.cooperativa.sistema.votacao.mapper;

import com.cooperativa.sistema.votacao.domain.Associado;
import com.cooperativa.sistema.votacao.dto.AssociadoDTO;
import com.cooperativa.sistema.votacao.dto.AssociadoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for Associado entity and DTOs
 */
@Mapper
public interface AssociadoMapper {
    
    /**
     * Convert entity to DTO
     */
    AssociadoDTO toDto(Associado associado);
    
    /**
     * Convert request to entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "ativo", constant = "true")
    Associado toEntity(AssociadoRequest request);
    
    /**
     * Convert list of entities to list of DTOs
     */
    List<AssociadoDTO> toDtoList(List<Associado> associados);
}