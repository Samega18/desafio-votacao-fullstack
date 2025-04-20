package com.cooperativa.sistema.votacao.service;

import com.cooperativa.sistema.votacao.domain.Associado;
import com.cooperativa.sistema.votacao.dto.AssociadoDTO;
import com.cooperativa.sistema.votacao.dto.AssociadoRequest;
import com.cooperativa.sistema.votacao.exception.CPFDuplicadoException;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.mapper.AssociadoMapper;
import com.cooperativa.sistema.votacao.repository.AssociadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing associates
 */
@Service
@Slf4j
public class AssociadoService {
    
    private final AssociadoRepository associadoRepository;
    private final AssociadoMapper mapper;
    
    @Autowired
    public AssociadoService(AssociadoRepository associadoRepository, AssociadoMapper mapper) {
        this.associadoRepository = associadoRepository;
        this.mapper = mapper;
    }
    
    /**
     * Register a new associate
     * 
     * @param request AssociadoRequest containing associate details
     * @return DTO with registered associate
     * @throws CPFDuplicadoException if CPF already exists
     */
    @Transactional
    public AssociadoDTO cadastrarAssociado(AssociadoRequest request) {
        log.info("Cadastrando novo associado com CPF: {}", request.getCpf());
        
        // Check if CPF already exists
        if (associadoRepository.existsByCpf(request.getCpf())) {
            log.warn("CPF já cadastrado: {}", request.getCpf());
            throw new CPFDuplicadoException("CPF já cadastrado no sistema");
        }
        
        // Create new associate
        Associado associado = mapper.toEntity(request);
        associado.setId(UUID.randomUUID().toString());
        
        try {
            associado = associadoRepository.save(associado);
        } catch (DataIntegrityViolationException e) {
            // Handle race condition: another concurrent request might have saved the same CPF
            log.warn("Conflito ao cadastrar associado: {}", e.getMessage());
            throw new CPFDuplicadoException("CPF já cadastrado no sistema");
        }
        
        log.info("Associado cadastrado com sucesso. ID: {}", associado.getId());
        return mapper.toDto(associado);
    }
    
    /**
     * Get a specific associate by ID
     * 
     * @param id ID of the associate
     * @return DTO of the associate
     * @throws ResourceNotFoundException if associate not found
     */
    @Transactional(readOnly = true)
    public AssociadoDTO obterAssociado(String id) {
        log.info("Buscando associado com ID: {}", id);
        
        Associado associado = associadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Associado não encontrado com o ID: " + id));
        
        return mapper.toDto(associado);
    }
    
    /**
     * List all associates with pagination
     * 
     * @param pageable Pagination information
     * @return List of associate DTOs
     */
    @Transactional(readOnly = true)
    public List<AssociadoDTO> listarAssociados(Pageable pageable) {
        log.info("Listando associados");
        return mapper.toDtoList(associadoRepository.findAll(pageable).getContent());
    }
    
    /**
     * Find an associate by CPF
     * 
     * @param cpf CPF of the associate
     * @return DTO of the associate
     * @throws ResourceNotFoundException if associate not found
     */
    @Transactional(readOnly = true)
    public AssociadoDTO buscarPorCpf(String cpf) {
        log.info("Buscando associado com CPF: {}", cpf);
        
        if (!StringUtils.hasText(cpf)) {
            throw new IllegalArgumentException("CPF não pode ser vazio");
        }
        
        Associado associado = associadoRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Associado não encontrado com o CPF: " + cpf));
        
        return mapper.toDto(associado);
    }
}