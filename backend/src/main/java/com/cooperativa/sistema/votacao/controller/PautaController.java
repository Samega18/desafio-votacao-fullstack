package com.cooperativa.sistema.votacao.controller;

import com.cooperativa.sistema.votacao.dto.PautaDTO;
import com.cooperativa.sistema.votacao.dto.PautaRequest;
import com.cooperativa.sistema.votacao.service.PautaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing voting agendas
 */
@RestController
@RequestMapping("/api/v1/pautas")
@Slf4j
public class PautaController {
    
    private final PautaService pautaService;
    
    @Autowired
    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }
    
    /**
     * POST /api/v1/pautas : Create a new agenda
     *
     * @param request PautaRequest containing agenda information
     * @return ResponseEntity with the created agenda and HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<PautaDTO> criarPauta(@Valid @RequestBody PautaRequest request) {
        log.info("REST request para criar nova pauta: {}", request);
        PautaDTO result = pautaService.criarPauta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * GET /api/v1/pautas : Get all agendas with pagination
     *
     * @param page Page number (default 0)
     * @param size Page size (default 10)
     * @return ResponseEntity with list of agendas
     */
    @GetMapping
    public ResponseEntity<List<PautaDTO>> listarPautas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request para listar pautas: page={}, size={}", page, size);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCriacao"));
        List<PautaDTO> result = pautaService.listarPautas(pageRequest);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * GET /api/v1/pautas/{id} : Get details of a specific agenda
     *
     * @param id Agenda ID
     * @return ResponseEntity with the agenda details
     */
    @GetMapping("/{id}")
    public ResponseEntity<PautaDTO> obterPauta(@PathVariable Long id) {
        log.info("REST request para obter pauta: {}", id);
        PautaDTO result = pautaService.obterPauta(id);
        return ResponseEntity.ok(result);
    }
}