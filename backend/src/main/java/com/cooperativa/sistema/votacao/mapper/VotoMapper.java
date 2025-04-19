package com.cooperativa.sistema.votacao.mapper;

import com.cooperativa.sistema.votacao.domain.Voto;
import com.cooperativa.sistema.votacao.domain.SessaoVotacao;
import com.cooperativa.sistema.votacao.dto.VotoDTO;
import com.cooperativa.sistema.votacao.dto.VotoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper interface for Voto entity and DTOs
 */
@Mapper
public interface VotoMapper {
    
    /**
     * Convert entity to DTO
     */
    VotoDTO toDto(Voto voto);
    
    /**
     * Convert request to entity with session
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataHoraVoto", ignore = true)
    @Mapping(target = "sessaoVotacao", source = "sessaoVotacao")
    @Mapping(target = "cpfAssociado", source = "request.cpf")
    Voto toEntity(VotoRequest request, SessaoVotacao sessaoVotacao);
}