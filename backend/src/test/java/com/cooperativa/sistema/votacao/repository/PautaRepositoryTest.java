package com.cooperativa.sistema.votacao.repository;

import com.cooperativa.sistema.votacao.domain.Pauta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PautaRepositoryTest {

    @Autowired
    private PautaRepository pautaRepository;

    @Test
    @DisplayName("Deve salvar uma pauta")
    void salvarPauta() {
        // Arrange
        Pauta pauta = Pauta.builder()
                .titulo("Pauta de Teste")
                .descricao("Descrição da pauta de teste")
                .dataCriacao(LocalDateTime.now())
                .build();

        // Act
        Pauta savedPauta = pautaRepository.save(pauta);

        // Assert
        assertThat(savedPauta).isNotNull();
        assertThat(savedPauta.getId()).isNotNull();
        assertThat(savedPauta.getTitulo()).isEqualTo("Pauta de Teste");
        assertThat(savedPauta.getDescricao()).isEqualTo("Descrição da pauta de teste");
        assertThat(savedPauta.getDataCriacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar uma pauta pelo ID")
    void buscarPautaPorId() {
        // Arrange
        Pauta pauta = Pauta.builder()
                .titulo("Pauta de Teste")
                .descricao("Descrição da pauta de teste")
                .dataCriacao(LocalDateTime.now())
                .build();
        Pauta savedPauta = pautaRepository.save(pauta);

        // Act
        Optional<Pauta> foundPauta = pautaRepository.findById(savedPauta.getId());

        // Assert
        assertTrue(foundPauta.isPresent());
        assertEquals("Pauta de Teste", foundPauta.get().getTitulo());
        assertEquals("Descrição da pauta de teste", foundPauta.get().getDescricao());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar pauta com ID inexistente")
    void buscarPautaInexistente() {
        // Act
        Optional<Pauta> foundPauta = pautaRepository.findById(999L);

        // Assert
        assertTrue(foundPauta.isEmpty());
    }

    @Test
    @DisplayName("Deve listar todas as pautas com paginação")
    void listarPautas() {
        // Arrange
        pautaRepository.save(Pauta.builder()
                .titulo("Pauta 1")
                .descricao("Descrição da pauta 1")
                .dataCriacao(LocalDateTime.now())
                .build());
        pautaRepository.save(Pauta.builder()
                .titulo("Pauta 2")
                .descricao("Descrição da pauta 2")
                .dataCriacao(LocalDateTime.now())
                .build());
        pautaRepository.save(Pauta.builder()
                .titulo("Pauta 3")
                .descricao("Descrição da pauta 3")
                .dataCriacao(LocalDateTime.now())
                .build());

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "titulo"));

        // Act
        Page<Pauta> pautasPage = pautaRepository.findAll(pageable);

        // Assert
        assertEquals(3, pautasPage.getTotalElements());
        assertEquals(2, pautasPage.getContent().size()); // Apenas 2 itens devido ao tamanho da página
        assertEquals("Pauta 1", pautasPage.getContent().get(0).getTitulo());
        assertEquals("Pauta 2", pautasPage.getContent().get(1).getTitulo());
    }

    @Test
    @DisplayName("Deve excluir uma pauta")
    void excluirPauta() {
        // Arrange
        Pauta pauta = Pauta.builder()
                .titulo("Pauta para Exclusão")
                .descricao("Descrição da pauta para exclusão")
                .dataCriacao(LocalDateTime.now())
                .build();
        Pauta savedPauta = pautaRepository.save(pauta);
        
        long countBefore = pautaRepository.count();

        // Act
        pautaRepository.deleteById(savedPauta.getId());
        
        // Assert
        long countAfter = pautaRepository.count();
        assertEquals(countBefore - 1, countAfter);
        assertFalse(pautaRepository.existsById(savedPauta.getId()));
    }

    @Test
    @DisplayName("Deve atualizar uma pauta")
    void atualizarPauta() {
        // Arrange
        Pauta pauta = Pauta.builder()
                .titulo("Pauta Original")
                .descricao("Descrição original")
                .dataCriacao(LocalDateTime.now())
                .build();
        Pauta savedPauta = pautaRepository.save(pauta);

        // Act
        savedPauta.setTitulo("Pauta Atualizada");
        savedPauta.setDescricao("Descrição atualizada");
        Pauta updatedPauta = pautaRepository.save(savedPauta);

        // Assert
        assertEquals(savedPauta.getId(), updatedPauta.getId());
        assertEquals("Pauta Atualizada", updatedPauta.getTitulo());
        assertEquals("Descrição atualizada", updatedPauta.getDescricao());
    }
} 