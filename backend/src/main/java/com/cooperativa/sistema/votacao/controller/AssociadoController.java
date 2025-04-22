package com.cooperativa.sistema.votacao.controller;

import com.cooperativa.sistema.votacao.dto.AssociadoDTO;
import com.cooperativa.sistema.votacao.dto.AssociadoRequest;
import com.cooperativa.sistema.votacao.service.AssociadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * REST controller for managing associates
 */
@RestController
@RequestMapping("/api/v1/associados")
@Slf4j
@Tag(name = "associados", description = "Gerenciamento de associados")
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
    
    /**
     * GET /api/v1/associados : List all associates
     *
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @return ResponseEntity with list of associates
     */
    @GetMapping
    @Operation(summary = "Listar todos os associados", description = "Retorna uma página de associados")
    @ApiResponse(responseCode = "200", description = "Associados encontrados com sucesso")
    public ResponseEntity<Page<AssociadoDTO>> listarAssociados(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size) {
        log.info("REST request para listar associados: page={}, size={}", page, size);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<AssociadoDTO> result = associadoService.listarAssociados(pageRequest);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * GET /api/v1/associados/busca : Search associates by name or CPF
     *
     * @param nome Name to search for (can be partial)
     * @param cpf CPF to search for (can be partial)
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @return ResponseEntity with list of associates matching the criteria
     */
    @GetMapping("/busca")
    @Operation(summary = "Buscar associados por nome ou CPF", description = "Retorna uma página de associados que correspondem aos critérios de busca")
    @ApiResponse(responseCode = "200", description = "Associados encontrados com sucesso")
    public ResponseEntity<Page<AssociadoDTO>> buscarAssociados(
            @Parameter(description = "Nome do associado (parcial)") @RequestParam(required = false) String nome,
            @Parameter(description = "CPF do associado (parcial)") @RequestParam(required = false) String cpf,
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size) {
        log.info("REST request para buscar associados por nome: {} ou CPF: {}, page={}, size={}", nome, cpf, page, size);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<AssociadoDTO> result = associadoService.buscarAssociados(nome, cpf, pageRequest);
        
        return ResponseEntity.ok(result);
    }
}