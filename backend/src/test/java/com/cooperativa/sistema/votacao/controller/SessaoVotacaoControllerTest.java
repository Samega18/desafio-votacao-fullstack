package com.cooperativa.sistema.votacao.controller;

import com.cooperativa.sistema.votacao.dto.ResultadoVotacaoDTO;
import com.cooperativa.sistema.votacao.dto.SessaoVotacaoDTO;
import com.cooperativa.sistema.votacao.dto.SessaoRequest;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.exception.SessaoJaExistenteException;
import com.cooperativa.sistema.votacao.service.SessaoVotacaoService;
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

@WebMvcTest(SessaoVotacaoController.class)
class SessaoVotacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessaoVotacaoService sessaoVotacaoService;

    private SessaoVotacaoDTO sessaoVotacaoDTO;
    private SessaoRequest sessaoRequest;
    private ResultadoVotacaoDTO resultadoVotacaoDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime agora = LocalDateTime.now();
        
        sessaoVotacaoDTO = SessaoVotacaoDTO.builder()
                .id(1L)
                .pautaId(1L)
                .tituloPauta("Pauta de Teste")
                .dataAbertura(agora)
                .dataFechamento(agora.plusMinutes(1))
                .encerrada(false)
                .build();

        sessaoRequest = new SessaoRequest();
        sessaoRequest.setDuracaoMinutos(1);
        
        resultadoVotacaoDTO = ResultadoVotacaoDTO.builder()
                .id(1L)
                .sessaoId(1L)
                .totalVotos(10)
                .votosSim(7)
                .votosNao(3)
                .aprovado(true)
                .dataApuracao(agora)
                .build();
    }

    @Test
    @DisplayName("Deve abrir uma nova sessão de votação com sucesso")
    void abrirSessao() throws Exception {
        when(sessaoVotacaoService.abrirSessao(eq(1L), any(SessaoRequest.class))).thenReturn(sessaoVotacaoDTO);

        mockMvc.perform(post("/api/v1/pautas/1/sessoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessaoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pautaId", is(1)))
                .andExpect(jsonPath("$.tituloPauta", is("Pauta de Teste")))
                .andExpect(jsonPath("$.dataAbertura").exists())
                .andExpect(jsonPath("$.dataFechamento").exists())
                .andExpect(jsonPath("$.encerrada", is(false)));

        verify(sessaoVotacaoService).abrirSessao(eq(1L), any(SessaoRequest.class));
    }
    
    @Test
    @DisplayName("Deve abrir uma nova sessão com duração padrão quando request for null")
    void abrirSessaoComDuracaoPadrao() throws Exception {
        when(sessaoVotacaoService.abrirSessao(eq(1L), any(SessaoRequest.class))).thenReturn(sessaoVotacaoDTO);

        mockMvc.perform(post("/api/v1/pautas/1/sessoes"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pautaId", is(1)));

        verify(sessaoVotacaoService).abrirSessao(eq(1L), any(SessaoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar abrir sessão para pauta inexistente")
    void abrirSessaoPautaInexistente() throws Exception {
        when(sessaoVotacaoService.abrirSessao(eq(99L), any(SessaoRequest.class)))
                .thenThrow(new ResourceNotFoundException("Pauta não encontrada com o ID: 99"));

        mockMvc.perform(post("/api/v1/pautas/99/sessoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessaoRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Pauta não encontrada")));

        verify(sessaoVotacaoService).abrirSessao(eq(99L), any(SessaoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar abrir sessão já existente")
    void abrirSessaoJaExistente() throws Exception {
        when(sessaoVotacaoService.abrirSessao(eq(1L), any(SessaoRequest.class)))
                .thenThrow(new SessaoJaExistenteException("Já existe uma sessão de votação aberta para esta pauta"));

        mockMvc.perform(post("/api/v1/pautas/1/sessoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessaoRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Já existe uma sessão")));

        verify(sessaoVotacaoService).abrirSessao(eq(1L), any(SessaoRequest.class));
    }

    @Test
    @DisplayName("Deve obter uma sessão de votação pelo ID")
    void obterSessao() throws Exception {
        when(sessaoVotacaoService.obterSessao(1L)).thenReturn(sessaoVotacaoDTO);

        mockMvc.perform(get("/api/v1/sessoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pautaId", is(1)))
                .andExpect(jsonPath("$.tituloPauta", is("Pauta de Teste")))
                .andExpect(jsonPath("$.dataAbertura").exists())
                .andExpect(jsonPath("$.dataFechamento").exists())
                .andExpect(jsonPath("$.encerrada", is(false)));

        verify(sessaoVotacaoService).obterSessao(1L);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar sessão com ID inexistente")
    void obterSessaoInexistente() throws Exception {
        when(sessaoVotacaoService.obterSessao(99L))
                .thenThrow(new ResourceNotFoundException("Sessão de votação não encontrada com o ID: 99"));

        mockMvc.perform(get("/api/v1/sessoes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Sessão de votação não encontrada")));

        verify(sessaoVotacaoService).obterSessao(99L);
    }

    @Test
    @DisplayName("Deve encerrar uma sessão de votação")
    void encerrarSessao() throws Exception {
        doNothing().when(sessaoVotacaoService).encerrarSessao(1L);

        mockMvc.perform(put("/api/v1/sessoes/1/encerrar"))
                .andExpect(status().isOk());

        verify(sessaoVotacaoService).encerrarSessao(1L);
    }

    @Test
    @DisplayName("Deve obter o resultado de uma sessão de votação")
    void obterResultado() throws Exception {
        when(sessaoVotacaoService.obterResultado(1L)).thenReturn(resultadoVotacaoDTO);

        mockMvc.perform(get("/api/v1/sessoes/1/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.sessaoId", is(1)))
                .andExpect(jsonPath("$.totalVotos", is(10)))
                .andExpect(jsonPath("$.votosSim", is(7)))
                .andExpect(jsonPath("$.votosNao", is(3)))
                .andExpect(jsonPath("$.aprovado", is(true)));

        verify(sessaoVotacaoService).obterResultado(1L);
    }

    @Test
    @DisplayName("Deve listar sessões de votação com paginação")
    void listarSessoes() throws Exception {
        List<SessaoVotacaoDTO> sessoes = Arrays.asList(
                sessaoVotacaoDTO,
                SessaoVotacaoDTO.builder()
                        .id(2L)
                        .pautaId(2L)
                        .tituloPauta("Outra Pauta")
                        .dataAbertura(LocalDateTime.now())
                        .dataFechamento(LocalDateTime.now().plusMinutes(5))
                        .encerrada(false)
                        .build()
        );

        PageImpl<SessaoVotacaoDTO> page = new PageImpl<>(sessoes, PageRequest.of(0, 10), 2);
        when(sessaoVotacaoService.listarSessoes(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/sessoes")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(sessaoVotacaoService).listarSessoes(any(PageRequest.class));
    }
} 