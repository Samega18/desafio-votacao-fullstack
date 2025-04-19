package com.cooperativa.sistema.votacao.controller;

import com.cooperativa.sistema.votacao.dto.ResultadoVotacaoDTO;
import com.cooperativa.sistema.votacao.dto.SessaoVotacaoDTO;
import com.cooperativa.sistema.votacao.dto.SessaoRequest;
import com.cooperativa.sistema.votacao.service.SessaoVotacaoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing voting sessions
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SessaoVotacaoController {
    
    private final SessaoVotacaoService sessaoService;
    
    @Autowired
    public SessaoVotacaoController(SessaoVotacaoService sessaoService) {
        this.sessaoService = sessaoService;
    }
    
    /**
     * POST /api/v1/pautas/{pautaId}/sessoes : Open a new voting session for an agenda
     *
     * @param pautaId ID of the agenda
     * @param request SessaoRequest with session details
     * @return ResponseEntity with the created session and HTTP status 201 (Created)
     */
    @PostMapping("/pautas/{pautaId}/sessoes")
    public ResponseEntity<SessaoVotacaoDTO> abrirSessao(
            @PathVariable Long pautaId,
            @Valid @RequestBody(required = false) SessaoRequest request) {
        log.info("REST request para abrir sessão de votação para pauta {}: {}", pautaId, request);
        
        // If request is null, create an empty one with default duration
        if (request == null) {
            request = new SessaoRequest();
            request.setDuracaoMinutos(1); // Default 1 minute
        }
        
        SessaoVotacaoDTO result = sessaoService.abrirSessao(pautaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * GET /api/v1/sessoes/{id} : Get details of a specific voting session
     *
     * @param id Session ID
     * @return ResponseEntity with the session details
     */
    @GetMapping("/sessoes/{id}")
    public ResponseEntity<SessaoVotacaoDTO> obterSessao(@PathVariable Long id) {
        log.info("REST request para obter sessão de votação: {}", id);
        SessaoVotacaoDTO result = sessaoService.obterSessao(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * PUT /api/v1/sessoes/{id}/encerrar : Close a voting session
     *
     * @param id Session ID
     * @return ResponseEntity with HTTP status 200 (OK)
     */
    @PutMapping("/sessoes/{id}/encerrar")
    public ResponseEntity<Void> encerrarSessao(@PathVariable Long id) {
        log.info("REST request para encerrar sessão de votação: {}", id);
        sessaoService.encerrarSessao(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * GET /api/v1/sessoes/{id}/resultado : Get voting results for a session
     *
     * @param id Session ID
     * @return ResponseEntity with the voting results
     */
    @GetMapping("/sessoes/{id}/resultado")
    public ResponseEntity<ResultadoVotacaoDTO> obterResultado(@PathVariable Long id) {
        log.info("REST request para obter resultado da sessão de votação: {}", id);
        ResultadoVotacaoDTO result = sessaoService.obterResultado(id);
        return ResponseEntity.ok(result);
    }
}