package com.cooperativa.sistema.votacao.controller;

import com.cooperativa.sistema.votacao.dto.VotoDTO;
import com.cooperativa.sistema.votacao.dto.VotoRequest;
import com.cooperativa.sistema.votacao.service.VotoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing votes
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class VotoController {
    
    private final VotoService votoService;
    
    @Autowired
    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }
    
    /**
     * POST /api/v1/sessoes/{sessaoId}/votos : Register a vote in a session
     *
     * @param sessaoId ID of the voting session
     * @param request VotoRequest containing vote details
     * @return ResponseEntity with the registered vote and HTTP status 201 (Created)
     */
    @PostMapping("/sessoes/{sessaoId}/votos")
    public ResponseEntity<VotoDTO> registrarVoto(
            @PathVariable Long sessaoId,
            @Valid @RequestBody VotoRequest request) {
        log.info("REST request para registrar voto na sessão {}: {}", sessaoId, request);
        VotoDTO result = votoService.registrarVoto(sessaoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}