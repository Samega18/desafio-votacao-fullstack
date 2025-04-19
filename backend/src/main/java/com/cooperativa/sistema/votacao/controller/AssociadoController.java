package com.cooperativa.sistema.votacao.controller;

import com.cooperativa.sistema.votacao.dto.AssociadoDTO;
import com.cooperativa.sistema.votacao.dto.AssociadoRequest;
import com.cooperativa.sistema.votacao.service.AssociadoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing associates
 */
@RestController
@RequestMapping("/api/v1/associados")
@Slf4j
public class AssociadoController {
    
    private final AssociadoService associadoService;
    
    @Autowired
    public AssociadoController(AssociadoService associadoService) {
        this.associadoService = associadoService;
    }
    
    /**
     * POST /api/v1/associados : Register a new associate
     *
     * @param request AssociadoRequest containing associate information
     * @return ResponseEntity with the registered associate and HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<AssociadoDTO> cadastrarAssociado(@Valid @RequestBody AssociadoRequest request) {
        log.info("REST request para cadastrar novo associado: {}", request);
        AssociadoDTO result = associadoService.cadastrarAssociado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * GET /api/v1/associados/{id} : Get details of a specific associate
     *
     * @param id Associate ID
     * @return ResponseEntity with the associate details
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssociadoDTO> obterAssociado(@PathVariable String id) {
        log.info("REST request para obter associado: {}", id);
        AssociadoDTO result = associadoService.obterAssociado(id);
        return ResponseEntity.ok(result);
    }
}