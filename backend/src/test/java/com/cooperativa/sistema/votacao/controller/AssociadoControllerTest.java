package com.cooperativa.sistema.votacao.controller;

import com.cooperativa.sistema.votacao.domain.Associado;
import com.cooperativa.sistema.votacao.dto.AssociadoDTO;
import com.cooperativa.sistema.votacao.dto.AssociadoRequest;
import com.cooperativa.sistema.votacao.exception.CPFDuplicadoException;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.service.AssociadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssociadoController.class)
class AssociadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssociadoService associadoService;

    private AssociadoDTO associadoDTO;
    private AssociadoRequest associadoRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        associadoDTO = AssociadoDTO.builder()
                .id("1")
                .nome("João Silva")
                .cpf("12345678900")
                .ativo(true)
                .build();

        associadoRequest = new AssociadoRequest();
        associadoRequest.setNome("João Silva");
        associadoRequest.setCpf("12345678900");
    }

    @Test
    @DisplayName("Deve cadastrar um novo associado com sucesso")
    void cadastrarAssociado() throws Exception {
        when(associadoService.cadastrarAssociado(any(AssociadoRequest.class))).thenReturn(associadoDTO);

        mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associadoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.nome", is("João Silva")))
                .andExpect(jsonPath("$.cpf", is("12345678900")))
                .andExpect(jsonPath("$.ativo", is(true)));

        verify(associadoService).cadastrarAssociado(any(AssociadoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao cadastrar associado com CPF duplicado")
    void cadastrarAssociadoCPFDuplicado() throws Exception {
        when(associadoService.cadastrarAssociado(any(AssociadoRequest.class)))
                .thenThrow(new CPFDuplicadoException("CPF já cadastrado no sistema"));

        mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associadoRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("CPF já cadastrado")));

        verify(associadoService).cadastrarAssociado(any(AssociadoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar um associado pelo ID")
    void obterAssociado() throws Exception {
        when(associadoService.obterAssociado("1")).thenReturn(associadoDTO);

        mockMvc.perform(get("/api/v1/associados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.nome", is("João Silva")))
                .andExpect(jsonPath("$.cpf", is("12345678900")));

        verify(associadoService).obterAssociado("1");
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar associado com ID inexistente")
    void obterAssociadoInexistente() throws Exception {
        when(associadoService.obterAssociado("999")).thenThrow(
                new ResourceNotFoundException("Associado não encontrado com o ID: 999"));

        mockMvc.perform(get("/api/v1/associados/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Associado não encontrado")));

        verify(associadoService).obterAssociado("999");
    }

    @Test
    @DisplayName("Deve listar associados com paginação")
    void listarAssociados() throws Exception {
        List<AssociadoDTO> associados = Arrays.asList(
                associadoDTO,
                AssociadoDTO.builder()
                        .id("2")
                        .nome("Maria Souza")
                        .cpf("98765432100")
                        .ativo(true)
                        .build()
        );

        PageImpl<AssociadoDTO> page = new PageImpl<>(associados, PageRequest.of(0, 10), 2);
        when(associadoService.listarAssociados(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/associados")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is("1")))
                .andExpect(jsonPath("$.content[1].id", is("2")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(associadoService).listarAssociados(any(PageRequest.class));
    }

    @Test
    @DisplayName("Deve buscar associados por nome")
    void buscarAssociadosPorNome() throws Exception {
        List<AssociadoDTO> associados = List.of(associadoDTO);
        PageImpl<AssociadoDTO> page = new PageImpl<>(associados, PageRequest.of(0, 10), 1);
        
        when(associadoService.buscarAssociados(eq("João"), eq(null), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/associados/busca")
                .param("nome", "João")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is("João Silva")));

        verify(associadoService).buscarAssociados(eq("João"), eq(null), any(PageRequest.class));
    }

    @Test
    @DisplayName("Deve buscar associados por CPF")
    void buscarAssociadosPorCPF() throws Exception {
        List<AssociadoDTO> associados = List.of(associadoDTO);
        PageImpl<AssociadoDTO> page = new PageImpl<>(associados, PageRequest.of(0, 10), 1);
        
        when(associadoService.buscarAssociados(eq(null), eq("12345"), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/associados/busca")
                .param("cpf", "12345")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].cpf", is("12345678900")));

        verify(associadoService).buscarAssociados(eq(null), eq("12345"), any(PageRequest.class));
    }
} 