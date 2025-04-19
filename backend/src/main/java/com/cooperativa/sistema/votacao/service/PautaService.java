package com.cooperativa.sistema.votacao.service;

import com.cooperativa.sistema.votacao.domain.Pauta;
import com.cooperativa.sistema.votacao.dto.PautaDTO;
import com.cooperativa.sistema.votacao.dto.PautaRequest;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.mapper.PautaMapper;
import com.cooperativa.sistema.votacao.repository.PautaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing voting agendas
 */
@Service
@Slf4j
public class PautaService {
    
    private final PautaRepository pautaRepository;
    private final PautaMapper mapper;
    
    @Autowired
    public PautaService(PautaRepository pautaRepository, PautaMapper mapper) {
        this.pautaRepository = pautaRepository;
        this.mapper = mapper;
    }
    
    /**
     * Create a new agenda
     * 
     * @param request PautaRequest containing agenda information
     * @return DTO with created agenda
     */
    @Transactional
    public PautaDTO criarPauta(PautaRequest request) {
        log.info("Criando nova pauta com título: {}", request.getTitulo());
        
        Pauta pauta = mapper.toEntity(request);
        pauta = pautaRepository.save(pauta);
        
        log.info("Pauta criada com sucesso. ID: {}", pauta.getId());
        return mapper.toDto(pauta);
    }
    
    /**
     * List all agendas with pagination
     * 
     * @param pageable Pagination information
     * @return List of agenda DTOs
     */
    @Transactional(readOnly = true)
    public List<PautaDTO> listarPautas(Pageable pageable) {
        log.info("Listando pautas");
        return mapper.toDtoList(pautaRepository.findAll(pageable).getContent());
    }
    
    /**
     * Get a specific agenda by ID
     * 
     * @param id ID of the agenda
     * @return DTO of the agenda
     * @throws ResourceNotFoundException if agenda not found
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "pautas", key = "#id")
    public PautaDTO obterPauta(Long id) {
        log.info("Buscando pauta com ID: {}", id);
        
        Pauta pauta = pautaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada com o ID: " + id));
        
        return mapper.toDto(pauta);
    }
    
    /**
     * Get a pauta entity by ID
     * 
     * @param id ID of the agenda
     * @return Pauta entity
     * @throws ResourceNotFoundException if agenda not found
     */
    @Transactional(readOnly = true)
    public Pauta obterPautaEntity(Long id) {
        log.info("Buscando entidade Pauta com ID: {}", id);
        
        return pautaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada com o ID: " + id));
    }
}