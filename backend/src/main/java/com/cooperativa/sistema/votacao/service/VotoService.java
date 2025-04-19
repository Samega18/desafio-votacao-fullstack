package com.cooperativa.sistema.votacao.service;

import com.cooperativa.sistema.votacao.client.CPFValidator;
import com.cooperativa.sistema.votacao.domain.SessaoVotacao;
import com.cooperativa.sistema.votacao.domain.Voto;
import com.cooperativa.sistema.votacao.dto.VotoDTO;
import com.cooperativa.sistema.votacao.dto.VotoRequest;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.exception.SessaoEncerradaException;
import com.cooperativa.sistema.votacao.exception.VotoJaRealizadoException;
import com.cooperativa.sistema.votacao.mapper.VotoMapper;
import com.cooperativa.sistema.votacao.repository.SessaoVotacaoRepository;
import com.cooperativa.sistema.votacao.repository.VotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing votes
 */
@Service
@Slf4j
public class VotoService {
    
    private final VotoRepository votoRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final CPFValidator cpfValidator;
    private final VotoMapper mapper;
    
    @Autowired
    public VotoService(
            VotoRepository votoRepository,
            SessaoVotacaoRepository sessaoRepository,
            CPFValidator cpfValidator,
            VotoMapper mapper) {
        this.votoRepository = votoRepository;
        this.sessaoRepository = sessaoRepository;
        this.cpfValidator = cpfValidator;
        this.mapper = mapper;
    }
    
    /**
     * Register a vote in a voting session
     * 
     * @param sessaoId ID of the voting session
     * @param request VotoRequest containing vote details
     * @return DTO with registered vote
     * @throws ResourceNotFoundException if session not found
     * @throws SessaoEncerradaException if session is closed
     * @throws VotoJaRealizadoException if associate already voted
     */
    @Transactional
    @CacheEvict(value = "resultados", key = "#sessaoId")
    public VotoDTO registrarVoto(Long sessaoId, VotoRequest request) {
        log.info("Registrando voto para sessão {}, associado {}", sessaoId, request.getIdAssociado());
        
        // Get session
        SessaoVotacao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de votação não encontrada com o ID: " + sessaoId));
        
        // Check if session is open
        validarSessaoAberta(sessao);
        
        // Check if associate already voted
        validarVotoUnico(request.getIdAssociado(), sessaoId);
        
        // Validate CPF
        validarCPF(request.getCpf());
        
        // Create and save vote
        Voto voto = mapper.toEntity(request, sessao);
        
        try {
            voto = votoRepository.save(voto);
        } catch (DataIntegrityViolationException e) {
            // Handle race condition: another concurrent vote might have been saved
            log.warn("Conflito ao registrar voto: {}", e.getMessage());
            throw new VotoJaRealizadoException("Associado já votou nesta sessão");
        }
        
        log.info("Voto registrado com sucesso. ID: {}", voto.getId());
        return mapper.toDto(voto);
    }
    
    /**
     * Validate if an associate can vote in a session
     * 
     * @param idAssociado ID of the associate
     * @param sessaoId ID of the voting session
     * @throws VotoJaRealizadoException if associate already voted
     */
    private void validarVotoUnico(String idAssociado, Long sessaoId) {
        if (votoRepository.existsBySessaoVotacaoIdAndIdAssociado(sessaoId, idAssociado)) {
            log.warn("Associado {} já votou na sessão {}", idAssociado, sessaoId);
            throw new VotoJaRealizadoException("Associado já votou nesta sessão");
        }
    }
    
    /**
     * Validate if a session is open for voting
     * 
     * @param sessao Session to validate
     * @throws SessaoEncerradaException if session is closed
     */
    private void validarSessaoAberta(SessaoVotacao sessao) {
        if (!sessao.isAberta()) {
            log.warn("Tentativa de voto em sessão {} não aberta", sessao.getId());
            throw new SessaoEncerradaException("Sessão de votação não está aberta");
        }
    }
    
    /**
     * Validate CPF and check if associate can vote
     * 
     * @param cpf CPF to validate
     */
    private void validarCPF(String cpf) {
        // This will throw exceptions if CPF is invalid or associate cannot vote
        cpfValidator.validarCPF(cpf);
    }
}