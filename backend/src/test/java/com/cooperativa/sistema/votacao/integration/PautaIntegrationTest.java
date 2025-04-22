package com.cooperativa.sistema.votacao.integration;

import com.cooperativa.sistema.votacao.dto.PautaDTO;
import com.cooperativa.sistema.votacao.dto.PautaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PautaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve executar o fluxo completo da criação e consulta de pauta")
    void fluxoCompletoPauta() throws Exception {
        // Arrange
        PautaRequest request = new PautaRequest();
        request.setTitulo("Pauta de Integração");
        request.setDescricao("Descrição da pauta de integração");

        // Passo 1: Criar uma nova pauta
        MvcResult criacaoResult = mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo", is("Pauta de Integração")))
                .andExpect(jsonPath("$.descricao", is("Descrição da pauta de integração")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        // Extrair o ID da pauta criada
        String responseJson = criacaoResult.getResponse().getContentAsString();
        PautaDTO pautaDTO = objectMapper.readValue(responseJson, PautaDTO.class);
        Long pautaId = pautaDTO.getId();
        
        assertNotNull(pautaId, "ID da pauta não deve ser nulo");

        // Passo 2: Buscar a pauta pelo ID
        mockMvc.perform(get("/api/v1/pautas/{id}", pautaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(pautaId.intValue())))
                .andExpect(jsonPath("$.titulo", is("Pauta de Integração")))
                .andExpect(jsonPath("$.descricao", is("Descrição da pauta de integração")));

        // Passo 3: Listar todas as pautas e verificar se a nova pauta está na lista
        mockMvc.perform(get("/api/v1/pautas")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].id", hasItem(pautaId.intValue())));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar buscar pauta com ID inexistente")
    void buscarPautaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Pauta não encontrada")));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar criar pauta com dados inválidos")
    void criarPautaDadosInvalidos() throws Exception {
        // Título vazio
        PautaRequest requestTituloVazio = new PautaRequest();
        requestTituloVazio.setTitulo("");
        requestTituloVazio.setDescricao("Descrição da pauta");

        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTituloVazio)))
                .andExpect(status().isBadRequest());

        // Título muito longo (mais de 100 caracteres, se houver validação)
        PautaRequest requestTituloLongo = new PautaRequest();
        requestTituloLongo.setTitulo("a".repeat(101)); // 101 caracteres
        requestTituloLongo.setDescricao("Descrição da pauta");

        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTituloLongo)))
                .andExpect(status().isBadRequest());
    }
} 