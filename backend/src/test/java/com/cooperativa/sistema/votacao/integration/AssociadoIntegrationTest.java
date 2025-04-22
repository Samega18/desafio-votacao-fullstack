package com.cooperativa.sistema.votacao.integration;

import com.cooperativa.sistema.votacao.dto.AssociadoDTO;
import com.cooperativa.sistema.votacao.dto.AssociadoRequest;
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

import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AssociadoIntegrationTest {

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
    @DisplayName("Deve executar o fluxo completo do cadastro e consulta de associado")
    void fluxoCompletoAssociado() throws Exception {
        // Gerar CPF aleatório para evitar duplicação
        String cpfAleatorio = gerarCpfAleatorio();
        
        // Arrange
        AssociadoRequest request = new AssociadoRequest();
        request.setNome("João Integração");
        request.setCpf(cpfAleatorio);

        // Passo 1: Cadastrar um novo associado
        MvcResult cadastroResult = mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("João Integração")))
                .andExpect(jsonPath("$.cpf", is(cpfAleatorio)))
                .andExpect(jsonPath("$.ativo", is(true)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        // Extrair o ID do associado cadastrado
        String responseJson = cadastroResult.getResponse().getContentAsString();
        AssociadoDTO associadoDTO = objectMapper.readValue(responseJson, AssociadoDTO.class);
        String associadoId = associadoDTO.getId();
        
        assertNotNull(associadoId, "ID do associado não deve ser nulo");

        // Passo 2: Buscar o associado pelo ID
        mockMvc.perform(get("/api/v1/associados/{id}", associadoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(associadoId)))
                .andExpect(jsonPath("$.nome", is("João Integração")))
                .andExpect(jsonPath("$.cpf", is(cpfAleatorio)));

        // Passo 3: Listar todos os associados e verificar se o novo associado está na lista
        mockMvc.perform(get("/api/v1/associados")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].id", hasItem(associadoId)));

        // Passo 4: Buscar associado por nome
        mockMvc.perform(get("/api/v1/associados/busca")
                .param("nome", "Integração")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].nome", hasItem(containsString("Integração"))));

        // Passo 5: Buscar associado por CPF
        String cpfParcial = cpfAleatorio.substring(0, 5);
        mockMvc.perform(get("/api/v1/associados/busca")
                .param("cpf", cpfParcial)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].cpf", hasItem(containsString(cpfParcial))));

        // Passo 6: Tentar cadastrar associado com mesmo CPF (deve falhar)
        mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("CPF já cadastrado")));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar buscar associado com ID inexistente")
    void buscarAssociadoInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/associados/{id}", "id-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Associado não encontrado")));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar cadastrar associado com dados inválidos")
    void cadastrarAssociadoDadosInvalidos() throws Exception {
        // CPF vazio
        AssociadoRequest requestCpfVazio = new AssociadoRequest();
        requestCpfVazio.setNome("José Silva");
        requestCpfVazio.setCpf("");

        mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestCpfVazio)))
                .andExpect(status().isBadRequest());

        // Nome vazio
        AssociadoRequest requestNomeVazio = new AssociadoRequest();
        requestNomeVazio.setNome("");
        requestNomeVazio.setCpf(gerarCpfAleatorio());

        mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestNomeVazio)))
                .andExpect(status().isBadRequest());
    }
} 