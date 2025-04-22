package com.cooperativa.sistema.votacao.controller;

import com.cooperativa.sistema.votacao.dto.PautaDTO;
import com.cooperativa.sistema.votacao.dto.PautaRequest;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.service.PautaService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PautaService pautaService;

    private PautaDTO pautaDTO;
    private PautaRequest pautaRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        pautaDTO = PautaDTO.builder()
                .id(1L)
                .titulo("Pauta de Teste")
                .descricao("Descrição da pauta de teste")
                .dataCriacao(LocalDateTime.now())
                .build();

        pautaRequest = new PautaRequest();
        pautaRequest.setTitulo("Pauta de Teste");
        pautaRequest.setDescricao("Descrição da pauta de teste");
    }

    @Test
    @DisplayName("Deve criar uma nova pauta com sucesso")
    void criarPauta() throws Exception {
        when(pautaService.criarPauta(any(PautaRequest.class))).thenReturn(pautaDTO);

        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pautaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.titulo", is("Pauta de Teste")))
                .andExpect(jsonPath("$.descricao", is("Descrição da pauta de teste")));

        verify(pautaService).criarPauta(any(PautaRequest.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar pauta com título vazio")
    void criarPautaComTituloVazio() throws Exception {
        PautaRequest requestInvalido = new PautaRequest();
        requestInvalido.setTitulo("");
        requestInvalido.setDescricao("Descrição da pauta");

        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(pautaService, never()).criarPauta(any(PautaRequest.class));
    }

    @Test
    @DisplayName("Deve listar pautas com paginação")
    void listarPautas() throws Exception {
        List<PautaDTO> pautas = Arrays.asList(
                pautaDTO,
                PautaDTO.builder()
                        .id(2L)
                        .titulo("Outra Pauta")
                        .descricao("Descrição da outra pauta")
                        .dataCriacao(LocalDateTime.now())
                        .build()
        );

        PageImpl<PautaDTO> page = new PageImpl<>(pautas, PageRequest.of(0, 10), 2);
        when(pautaService.listarPautas(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/pautas")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(pautaService).listarPautas(any(PageRequest.class));
    }

    @Test
    @DisplayName("Deve obter uma pauta específica pelo ID")
    void obterPauta() throws Exception {
        when(pautaService.obterPauta(1L)).thenReturn(pautaDTO);

        mockMvc.perform(get("/api/v1/pautas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.titulo", is("Pauta de Teste")))
                .andExpect(jsonPath("$.descricao", is("Descrição da pauta de teste")));

        verify(pautaService).obterPauta(1L);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar pauta com ID inexistente")
    void obterPautaInexistente() throws Exception {
        when(pautaService.obterPauta(99L))
                .thenThrow(new ResourceNotFoundException("Pauta não encontrada com o ID: 99"));

        mockMvc.perform(get("/api/v1/pautas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Pauta não encontrada")));

        verify(pautaService).obterPauta(99L);
    }
} 