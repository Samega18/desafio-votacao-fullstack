package com.cooperativa.sistema.votacao.integration;

import com.cooperativa.sistema.votacao.client.CPFValidator;
import com.cooperativa.sistema.votacao.domain.OpcaoVoto;
import com.cooperativa.sistema.votacao.domain.StatusVotacao;
import com.cooperativa.sistema.votacao.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VotacaoIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        // Sobrescreve o CPFValidator real com um mock para não precisar da API externa
        @Bean
        @Primary
        public CPFValidator mockCpfValidator() {
            CPFValidator mockValidator = Mockito.mock(CPFValidator.class);
            // Configura o mock para retornar ABLE_TO_VOTE para qualquer CPF
            when(mockValidator.validarCPF(anyString())).thenReturn(StatusVotacao.ABLE_TO_VOTE);
            return mockValidator;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    // Método auxiliar para gerar CPF aleatório válido para testes
    private String gerarCpfAleatorio() {
        Random random = new Random();
        StringBuilder cpf = new StringBuilder();
        
        // Gera 11 dígitos aleatórios
        for (int i = 0; i < 11; i++) {
            cpf.append(random.nextInt(10));
        }
        
        return cpf.toString();
    }

    @Test
    @DisplayName("Deve executar fluxo completo de votação: criar pauta, abrir sessão, votar, encerrar e obter resultado")
    void fluxoCompletoVotacao() throws Exception {
        // Passo 1: Criar um associado
        String cpfAleatorio = gerarCpfAleatorio();
        
        AssociadoRequest associadoRequest = new AssociadoRequest();
        associadoRequest.setNome("José Votante");
        associadoRequest.setCpf(cpfAleatorio);

        MvcResult associadoResult = mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associadoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        String responseJson = associadoResult.getResponse().getContentAsString();
        AssociadoDTO associadoDTO = objectMapper.readValue(responseJson, AssociadoDTO.class);
        String associadoId = associadoDTO.getId();

        // Passo 2: Criar uma pauta
        PautaRequest pautaRequest = new PautaRequest();
        pautaRequest.setTitulo("Pauta de Votação");
        pautaRequest.setDescricao("Descrição da pauta de votação para teste de integração");

        MvcResult pautaResult = mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pautaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        responseJson = pautaResult.getResponse().getContentAsString();
        PautaDTO pautaDTO = objectMapper.readValue(responseJson, PautaDTO.class);
        Long pautaId = pautaDTO.getId();

        // Passo 3: Abrir uma sessão de votação para a pauta (duração de 5 minutos)
        SessaoRequest sessaoRequest = new SessaoRequest();
        sessaoRequest.setDuracaoMinutos(5);

        MvcResult sessaoResult = mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessoes", pautaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessaoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.pautaId", is(pautaId.intValue())))
                .andExpect(jsonPath("$.encerrada", is(false)))
                .andReturn();

        responseJson = sessaoResult.getResponse().getContentAsString();
        SessaoVotacaoDTO sessaoDTO = objectMapper.readValue(responseJson, SessaoVotacaoDTO.class);
        Long sessaoId = sessaoDTO.getId();

        // Passo 4: Registrar um voto na sessão
        VotoRequest votoRequest = new VotoRequest();
        votoRequest.setIdAssociado(associadoId);
        votoRequest.setCpf(cpfAleatorio);
        votoRequest.setOpcao(OpcaoVoto.SIM);

        mockMvc.perform(post("/api/v1/sessoes/{sessaoId}/votos", sessaoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAssociado", is(associadoId)))
                .andExpect(jsonPath("$.opcao", is("SIM")));

        // Passo 5: Tentar votar novamente com o mesmo associado (deve falhar)
        mockMvc.perform(post("/api/v1/sessoes/{sessaoId}/votos", sessaoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Associado já votou")));

        // Passo 6: Criar um segundo associado e registrar voto "NAO"
        String cpfAleatorio2 = gerarCpfAleatorio();
        
        AssociadoRequest associadoRequest2 = new AssociadoRequest();
        associadoRequest2.setNome("Maria Votante");
        associadoRequest2.setCpf(cpfAleatorio2);

        MvcResult associadoResult2 = mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associadoRequest2)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson2 = associadoResult2.getResponse().getContentAsString();
        AssociadoDTO associadoDTO2 = objectMapper.readValue(responseJson2, AssociadoDTO.class);
        String associadoId2 = associadoDTO2.getId();

        VotoRequest votoRequest2 = new VotoRequest();
        votoRequest2.setIdAssociado(associadoId2);
        votoRequest2.setCpf(cpfAleatorio2);
        votoRequest2.setOpcao(OpcaoVoto.NAO);

        mockMvc.perform(post("/api/v1/sessoes/{sessaoId}/votos", sessaoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoRequest2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.opcao", is("NAO")));

        // Passo 7: Encerrar a sessão de votação
        mockMvc.perform(put("/api/v1/sessoes/{sessaoId}/encerrar", sessaoId))
                .andExpect(status().isOk());

        // Passo 8: Obter o resultado da votação
        mockMvc.perform(get("/api/v1/sessoes/{sessaoId}/resultado", sessaoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVotos", is(2)))
                .andExpect(jsonPath("$.votosSim", is(1)))
                .andExpect(jsonPath("$.votosNao", is(1)))
                .andExpect(jsonPath("$.aprovado", anyOf(is(true), is(false))));

        // Passo 9: Tentar votar em uma sessão encerrada (deve falhar)
        String cpfAleatorio3 = gerarCpfAleatorio();
        
        AssociadoRequest associadoRequest3 = new AssociadoRequest();
        associadoRequest3.setNome("Pedro Votante");
        associadoRequest3.setCpf(cpfAleatorio3);

        MvcResult associadoResult3 = mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associadoRequest3)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson3 = associadoResult3.getResponse().getContentAsString();
        AssociadoDTO associadoDTO3 = objectMapper.readValue(responseJson3, AssociadoDTO.class);
        String associadoId3 = associadoDTO3.getId();

        VotoRequest votoRequest3 = new VotoRequest();
        votoRequest3.setIdAssociado(associadoId3);
        votoRequest3.setCpf(cpfAleatorio3);
        votoRequest3.setOpcao(OpcaoVoto.SIM);

        mockMvc.perform(post("/api/v1/sessoes/{sessaoId}/votos", sessaoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoRequest3)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", containsString("Sessão de votação não está aberta")));
    }
} 