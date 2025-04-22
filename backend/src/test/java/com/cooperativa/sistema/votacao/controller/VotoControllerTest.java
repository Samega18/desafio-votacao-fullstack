package com.cooperativa.sistema.votacao.controller;

import com.cooperativa.sistema.votacao.domain.OpcaoVoto;
import com.cooperativa.sistema.votacao.dto.VotoDTO;
import com.cooperativa.sistema.votacao.dto.VotoRequest;
import com.cooperativa.sistema.votacao.exception.CPFInvalidoException;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.exception.SessaoEncerradaException;
import com.cooperativa.sistema.votacao.exception.VotoJaRealizadoException;
import com.cooperativa.sistema.votacao.service.VotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VotoController.class)
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VotoService votoService;

    private VotoDTO votoDTO;
    private VotoRequest votoRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        votoDTO = VotoDTO.builder()
                .id(1L)
                .idAssociado("1")
                .opcao(OpcaoVoto.SIM)
                .dataHoraVoto(LocalDateTime.now())
                .build();

        votoRequest = new VotoRequest();
        votoRequest.setIdAssociado("1");
        votoRequest.setCpf("12345678900");
        votoRequest.setOpcao(OpcaoVoto.SIM);
    }

    @Test
    @DisplayName("Deve registrar um voto com sucesso")
    void registrarVoto() throws Exception {
        when(votoService.registrarVoto(eq(1L), any(VotoRequest.class))).thenReturn(votoDTO);

        mockMvc.perform(post("/api/v1/sessoes/1/votos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.idAssociado", is("1")))
                .andExpect(jsonPath("$.opcao", is("SIM")));

        verify(votoService).registrarVoto(eq(1L), any(VotoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar registrar voto em sessão inexistente")
    void registrarVotoSessaoInexistente() throws Exception {
        when(votoService.registrarVoto(eq(99L), any(VotoRequest.class)))
                .thenThrow(new ResourceNotFoundException("Sessão de votação não encontrada com o ID: 99"));

        mockMvc.perform(post("/api/v1/sessoes/99/votos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Sessão de votação não encontrada")));

        verify(votoService).registrarVoto(eq(99L), any(VotoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar registrar voto em sessão encerrada")
    void registrarVotoSessaoEncerrada() throws Exception {
        when(votoService.registrarVoto(eq(1L), any(VotoRequest.class)))
                .thenThrow(new SessaoEncerradaException("Sessão de votação não está aberta"));

        mockMvc.perform(post("/api/v1/sessoes/1/votos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", containsString("Sessão de votação não está aberta")));

        verify(votoService).registrarVoto(eq(1L), any(VotoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar registrar voto duplicado")
    void registrarVotoDuplicado() throws Exception {
        when(votoService.registrarVoto(eq(1L), any(VotoRequest.class)))
                .thenThrow(new VotoJaRealizadoException("Associado já votou nesta sessão"));

        mockMvc.perform(post("/api/v1/sessoes/1/votos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Associado já votou nesta sessão")));

        verify(votoService).registrarVoto(eq(1L), any(VotoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar registrar voto com CPF inválido")
    void registrarVotoCPFInvalido() throws Exception {
        when(votoService.registrarVoto(eq(1L), any(VotoRequest.class)))
                .thenThrow(new CPFInvalidoException("CPF inválido"));

        mockMvc.perform(post("/api/v1/sessoes/1/votos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("CPF inválido")));

        verify(votoService).registrarVoto(eq(1L), any(VotoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar registrar voto com dados incompletos")
    void registrarVotoDadosIncompletos() throws Exception {
        VotoRequest requestInvalido = new VotoRequest();
        // Faltando idAssociado

        mockMvc.perform(post("/api/v1/sessoes/1/votos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(votoService, never()).registrarVoto(anyLong(), any(VotoRequest.class));
    }
} 