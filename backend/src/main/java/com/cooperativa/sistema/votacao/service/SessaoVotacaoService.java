package com.cooperativa.sistema.votacao.service;

import com.cooperativa.sistema.votacao.domain.Pauta;
import com.cooperativa.sistema.votacao.domain.ResultadoVotacao;
import com.cooperativa.sistema.votacao.domain.SessaoVotacao;
import com.cooperativa.sistema.votacao.domain.Voto;
import com.cooperativa.sistema.votacao.domain.OpcaoVoto;
import com.cooperativa.sistema.votacao.dto.ResultadoVotacaoDTO;
import com.cooperativa.sistema.votacao.dto.SessaoVotacaoDTO;
import com.cooperativa.sistema.votacao.dto.SessaoRequest;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.exception.SessaoJaExistenteException;
import com.cooperativa.sistema.votacao.mapper.ResultadoVotacaoMapper;
import com.cooperativa.sistema.votacao.mapper.SessaoVotacaoMapper;
import com.cooperativa.sistema.votacao.repository.PautaRepository;
import com.cooperativa.sistema.votacao.repository.ResultadoVotacaoRepository;
import com.cooperativa.sistema.votacao.repository.SessaoVotacaoRepository;
import com.cooperativa.sistema.votacao.repository.VotoRepository;
import com.cooperativa.sistema.votacao.scheduler.SessaoScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing voting sessions
 */
@Service
@Slf4j
public class SessaoVotacaoService {
    
    private final SessaoVotacaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;
    private final ResultadoVotacaoRepository resultadoRepository;
    private final VotoRepository votoRepository;
    private final SessaoVotacaoMapper mapper;
    private final ResultadoVotacaoMapper resultadoMapper;
    private final SessaoScheduler sessaoScheduler;
    
    @Autowired
    public SessaoVotacaoService(
            SessaoVotacaoRepository sessaoRepository,
            PautaRepository pautaRepository,
            ResultadoVotacaoRepository resultadoRepository,
            VotoRepository votoRepository,
            SessaoVotacaoMapper mapper,
            ResultadoVotacaoMapper resultadoMapper,
            @Lazy SessaoScheduler sessaoScheduler) {
        this.sessaoRepository = sessaoRepository;
        this.pautaRepository = pautaRepository;
        this.resultadoRepository = resultadoRepository;
        this.votoRepository = votoRepository;
        this.mapper = mapper;
        this.resultadoMapper = resultadoMapper;
        this.sessaoScheduler = sessaoScheduler;
    }
    
    /**
     * Open a new voting session for an agenda
     * 
     * @param pautaId ID of the agenda
     * @param request SessaoRequest with session details
     * @return DTO of the created session
     * @throws ResourceNotFoundException if agenda not found
     * @throws SessaoJaExistenteException if there's an active session for the agenda
     */
    @Transactional
    public SessaoVotacaoDTO abrirSessao(Long pautaId, SessaoRequest request) {
        log.info("Abrindo sessão de votação para pauta ID: {}", pautaId);
        
        // Check if pauta exists
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada com o ID: " + pautaId));
        
        // Check if there's already an open session for this pauta
        if (sessaoRepository.existsOpenSessionForPauta(pautaId, LocalDateTime.now())) {
            throw new SessaoJaExistenteException("Já existe uma sessão de votação aberta para esta pauta");
        }
        
        // Create new session
        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPauta(pauta);
        sessao.setDataAbertura(LocalDateTime.now());
        
        // Set duration (default: 1 minute)
        Integer duracaoMinutos = request.getDuracaoMinutos();
        if (duracaoMinutos != null && duracaoMinutos > 0) {
            sessao.setDataFechamento(sessao.getDataAbertura().plusMinutes(duracaoMinutos));
        } else {
            sessao.setDataFechamento(sessao.getDataAbertura().plusMinutes(1));
        }
        
        sessao = sessaoRepository.save(sessao);
        
        // Schedule session closing
        sessaoScheduler.agendarFechamento(sessao);
        
        log.info("Sessão de votação aberta com sucesso. ID: {}, fechamento: {}", 
                sessao.getId(), sessao.getDataFechamento());
        
        return mapper.toDto(sessao);
    }
    
    /**
     * Get a specific voting session
     * 
     * @param id ID of the session
     * @return DTO of the session
     * @throws ResourceNotFoundException if session not found
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "sessoes", key = "#id")
    public SessaoVotacaoDTO obterSessao(Long id) {
        log.info("Buscando sessão de votação com ID: {}", id);
        
        SessaoVotacao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de votação não encontrada com o ID: " + id));
        
        return mapper.toDto(sessao);
    }
    
    /**
     * Get session entity by ID
     * 
     * @param id ID of the session
     * @return Session entity
     * @throws ResourceNotFoundException if session not found
     */
    @Transactional(readOnly = true)
    public SessaoVotacao obterSessaoEntity(Long id) {
        log.info("Buscando entidade SessaoVotacao com ID: {}", id);
        
        return sessaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de votação não encontrada com o ID: " + id));
    }
    
    /**
     * Close a voting session and calculate results
     * 
     * @param id ID of the session to close
     */
    @Transactional
    @CacheEvict(value = "sessoes", key = "#id")
    public void encerrarSessao(Long id) {
        log.info("Encerrando sessão de votação com ID: {}", id);
        
        SessaoVotacao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de votação não encontrada com o ID: " + id));
        
        // Check if session is already closed
        if (sessao.getEncerrada()) {
            log.info("Sessão de votação já encerrada. ID: {}", id);
            return;
        }
        
        // Close session
        sessao.setEncerrada(true);
        sessaoRepository.save(sessao);
        
        // Generate result if not exists
        if (!resultadoRepository.existsBySessaoVotacaoId(id)) {
            log.info("Gerando resultado para sessão de votação com ID: {}", id);
            calcularResultado(sessao);
        }
        
        log.info("Sessão de votação encerrada com sucesso. ID: {}", id);
    }
    
    /**
     * Calculate and save voting results
     * 
     * @param sessao Voting session
     * @return Result entity
     */
    @Transactional
    public ResultadoVotacao calcularResultado(SessaoVotacao sessao) {
        log.info("Calculando resultado para sessão de votação com ID: {}", sessao.getId());
        
        // Check if result already exists
        if (resultadoRepository.existsBySessaoVotacaoId(sessao.getId())) {
            return resultadoRepository.findBySessaoVotacaoId(sessao.getId())
                    .orElseThrow(() -> new IllegalStateException("Resultados inconsistentes para sessão " + sessao.getId()));
        }
        
        // Get vote counts
        Integer totalVotos = (int) votoRepository.countBySessaoVotacaoId(sessao.getId());
        Integer votosSim = (int) votoRepository.countBySessaoVotacaoIdAndOpcao(sessao.getId(), OpcaoVoto.SIM);
        Integer votosNao = (int) votoRepository.countBySessaoVotacaoIdAndOpcao(sessao.getId(), OpcaoVoto.NAO);
        
        // Create and save result
        ResultadoVotacao resultado = ResultadoVotacao.builder()
                .sessaoVotacao(sessao)
                .totalVotos(totalVotos)
                .votosSim(votosSim)
                .votosNao(votosNao)
                .dataApuracao(LocalDateTime.now())
                .build();
        
        resultado = resultadoRepository.save(resultado);
        
        log.info("Resultado calculado para sessão {}: Total={}, Sim={}, Não={}, Aprovado={}",
                sessao.getId(), totalVotos, votosSim, votosNao, resultado.isAprovado());
        
        return resultado;
    }
    
    /**
     * Get voting results for a session
     * 
     * @param sessaoId ID of the session
     * @return DTO with voting results
     * @throws ResourceNotFoundException if session not found
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "resultados", key = "#sessaoId")
    public ResultadoVotacaoDTO obterResultado(Long sessaoId) {
        log.info("Buscando resultado para sessão de votação com ID: {}", sessaoId);
        
        // Get session
        SessaoVotacao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de votação não encontrada com o ID: " + sessaoId));
        
        // Get or calculate result
        ResultadoVotacao resultado;
        if (resultadoRepository.existsBySessaoVotacaoId(sessaoId)) {
            resultado = resultadoRepository.findBySessaoVotacaoId(sessaoId)
                    .orElseThrow(() -> new IllegalStateException("Resultados inconsistentes para sessão " + sessaoId));
        } else {
            // If session is still open, calculate partial results without persisting
            if (!sessao.getEncerrada()) {
                Integer totalVotos = (int) votoRepository.countBySessaoVotacaoId(sessaoId);
                Integer votosSim = (int) votoRepository.countBySessaoVotacaoIdAndOpcao(sessaoId, OpcaoVoto.SIM);
                Integer votosNao = (int) votoRepository.countBySessaoVotacaoIdAndOpcao(sessaoId, OpcaoVoto.NAO);
                
                resultado = ResultadoVotacao.builder()
                        .sessaoVotacao(sessao)
                        .totalVotos(totalVotos)
                        .votosSim(votosSim)
                        .votosNao(votosNao)
                        .dataApuracao(LocalDateTime.now())
                        .build();
                
                log.info("Resultados parciais para sessão {}: Total={}, Sim={}, Não={}", 
                        sessaoId, totalVotos, votosSim, votosNao);
            } else {
                // If session is closed, calculate and persist results
                resultado = calcularResultado(sessao);
            }
        }
        
        return resultadoMapper.toDto(resultado);
    }
    
    /**
     * List all voting sessions with pagination
     * 
     * @param pageable Pagination information
     * @return List of session DTOs
     */
    @Transactional(readOnly = true)
    public List<SessaoVotacaoDTO> listarSessoes(Pageable pageable) {
        log.info("Listando sessões de votação");
        return mapper.toDtoList(sessaoRepository.findAll(pageable).getContent());
    }
}