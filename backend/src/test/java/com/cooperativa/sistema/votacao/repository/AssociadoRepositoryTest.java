package com.cooperativa.sistema.votacao.repository;

import com.cooperativa.sistema.votacao.domain.Associado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AssociadoRepositoryTest {

    @Autowired
    private AssociadoRepository associadoRepository;

    @Test
    @DisplayName("Deve salvar um associado")
    void salvarAssociado() {
        // Arrange
        Associado associado = Associado.builder()
                .id("1")
                .nome("João Silva")
                .cpf("12345678900")
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .build();

        // Act
        Associado saved = associadoRepository.save(associado);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo("1");
        assertThat(saved.getNome()).isEqualTo("João Silva");
        assertThat(saved.getCpf()).isEqualTo("12345678900");
        assertThat(saved.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve buscar associado por CPF")
    void findByCpf() {
        // Arrange
        Associado associado = Associado.builder()
                .id("1")
                .nome("João Silva")
                .cpf("12345678900")
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .build();
        associadoRepository.save(associado);

        // Act
        Optional<Associado> found = associadoRepository.findByCpf("12345678900");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("João Silva", found.get().getNome());
    }

    @Test
    @DisplayName("Deve verificar se existe associado por CPF")
    void existsByCpf() {
        // Arrange
        Associado associado = Associado.builder()
                .id("1")
                .nome("João Silva")
                .cpf("12345678900")
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .build();
        associadoRepository.save(associado);

        // Act & Assert
        assertTrue(associadoRepository.existsByCpf("12345678900"));
        assertFalse(associadoRepository.existsByCpf("99999999999"));
    }

    @Test
    @DisplayName("Deve buscar associados por nome contendo texto")
    void findByNomeContainingIgnoreCaseOrCpfContaining_Nome() {
        // Arrange
        associadoRepository.save(Associado.builder()
                .id("1")
                .nome("João Silva")
                .cpf("12345678900")
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .build());
        associadoRepository.save(Associado.builder()
                .id("2")
                .nome("Maria Silva")
                .cpf("98765432100")
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .build());
        associadoRepository.save(Associado.builder()
                .id("3")
                .nome("Pedro Souza")
                .cpf("11122233344")
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .build());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataCadastro"));

        // Act
        Page<Associado> result = associadoRepository.findByNomeContainingIgnoreCaseOrCpfContaining(
                "Silva", null, pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(a -> a.getNome().contains("Silva")));
    }

    @Test
    @DisplayName("Deve buscar associados por CPF contendo texto")
    void findByNomeContainingIgnoreCaseOrCpfContaining_CPF() {
        // Arrange
        associadoRepository.save(Associado.builder()
                .id("1")
                .nome("João Silva")
                .cpf("12345678900")
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .build());
        associadoRepository.save(Associado.builder()
                .id("2")
                .nome("Maria Souza")
                .cpf("98765432100")
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .build());
        associadoRepository.save(Associado.builder()
                .id("3")
                .nome("Pedro Oliveira")
                .cpf("11122233344")
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .build());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataCadastro"));

        // Act
        Page<Associado> result = associadoRepository.findByNomeContainingIgnoreCaseOrCpfContaining(
                null, "123", pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(a -> a.getCpf().contains("123")));
    }
} 