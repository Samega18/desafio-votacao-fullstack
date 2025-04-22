package com.cooperativa.sistema.votacao.service;

import com.cooperativa.sistema.votacao.domain.Associado;
import com.cooperativa.sistema.votacao.dto.AssociadoDTO;
import com.cooperativa.sistema.votacao.dto.AssociadoRequest;
import com.cooperativa.sistema.votacao.exception.CPFDuplicadoException;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.mapper.AssociadoMapper;
import com.cooperativa.sistema.votacao.repository.AssociadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssociadoServiceTest {

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private AssociadoMapper associadoMapper;

    @InjectMocks
    private AssociadoService associadoService;

    private Associado associado;
    private AssociadoDTO associadoDTO;
    private AssociadoRequest associadoRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        associado = Associado.builder()
                .id("1")
                .nome("João Silva")
                .cpf("12345678900")
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .build();

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
    void cadastrarAssociado() {
        // Arrange
        when(associadoRepository.existsByCpf(anyString())).thenReturn(false);
        when(associadoMapper.toEntity(any(AssociadoRequest.class))).thenReturn(associado);
        when(associadoRepository.save(any(Associado.class))).thenReturn(associado);
        when(associadoMapper.toDto(any(Associado.class))).thenReturn(associadoDTO);

        // Act
        AssociadoDTO result = associadoService.cadastrarAssociado(associadoRequest);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("João Silva", result.getNome());
        assertEquals("12345678900", result.getCpf());
        assertTrue(result.getAtivo());

        verify(associadoRepository).existsByCpf(associadoRequest.getCpf());
        verify(associadoMapper).toEntity(associadoRequest);
        verify(associadoRepository).save(any(Associado.class));
        verify(associadoMapper).toDto(associado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar associado com CPF duplicado")
    void cadastrarAssociadoCPFDuplicado() {
        // Arrange
        when(associadoRepository.existsByCpf(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(CPFDuplicadoException.class, () -> {
            associadoService.cadastrarAssociado(associadoRequest);
        });

        verify(associadoRepository).existsByCpf(associadoRequest.getCpf());
        verify(associadoRepository, never()).save(any(Associado.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer violação de integridade ao salvar")
    void cadastrarAssociadoDataIntegrityViolation() {
        // Arrange
        when(associadoRepository.existsByCpf(anyString())).thenReturn(false);
        when(associadoMapper.toEntity(any(AssociadoRequest.class))).thenReturn(associado);
        when(associadoRepository.save(any(Associado.class)))
                .thenThrow(new DataIntegrityViolationException("Erro de integridade"));

        // Act & Assert
        assertThrows(CPFDuplicadoException.class, () -> {
            associadoService.cadastrarAssociado(associadoRequest);
        });

        verify(associadoRepository).existsByCpf(associadoRequest.getCpf());
        verify(associadoMapper).toEntity(associadoRequest);
        verify(associadoRepository).save(any(Associado.class));
    }

    @Test
    @DisplayName("Deve obter um associado pelo ID")
    void obterAssociado() {
        // Arrange
        when(associadoRepository.findById(anyString())).thenReturn(Optional.of(associado));
        when(associadoMapper.toDto(any(Associado.class))).thenReturn(associadoDTO);

        // Act
        AssociadoDTO result = associadoService.obterAssociado("1");

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("João Silva", result.getNome());
        assertEquals("12345678900", result.getCpf());

        verify(associadoRepository).findById("1");
        verify(associadoMapper).toDto(associado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar associado com ID inexistente")
    void obterAssociadoInexistente() {
        // Arrange
        when(associadoRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            associadoService.obterAssociado("999");
        });

        verify(associadoRepository).findById("999");
        verify(associadoMapper, never()).toDto(any(Associado.class));
    }

    @Test
    @DisplayName("Deve listar todos os associados com paginação")
    void listarAssociados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Associado> associados = Arrays.asList(
                associado,
                Associado.builder()
                        .id("2")
                        .nome("Maria Souza")
                        .cpf("98765432100")
                        .dataCadastro(LocalDateTime.now())
                        .ativo(true)
                        .build()
        );
        Page<Associado> associadosPage = new PageImpl<>(associados, pageable, 2);

        when(associadoRepository.findAll(any(Pageable.class))).thenReturn(associadosPage);
        when(associadoMapper.toDto(associados.get(0))).thenReturn(associadoDTO);
        when(associadoMapper.toDto(associados.get(1))).thenReturn(
                AssociadoDTO.builder()
                        .id("2")
                        .nome("Maria Souza")
                        .cpf("98765432100")
                        .ativo(true)
                        .build()
        );

        // Act
        Page<AssociadoDTO> result = associadoService.listarAssociados(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("João Silva", result.getContent().get(0).getNome());
        assertEquals("Maria Souza", result.getContent().get(1).getNome());

        verify(associadoRepository).findAll(pageable);
        verify(associadoMapper, times(2)).toDto(any(Associado.class));
    }

    @Test
    @DisplayName("Deve buscar associados por nome")
    void buscarAssociadosPorNome() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Associado> associados = List.of(associado);
        Page<Associado> associadosPage = new PageImpl<>(associados, pageable, 1);

        when(associadoRepository.findByNomeContainingIgnoreCaseOrCpfContaining(
                eq("João"), eq(null), any(Pageable.class))).thenReturn(associadosPage);
        when(associadoMapper.toDto(any(Associado.class))).thenReturn(associadoDTO);

        // Act
        Page<AssociadoDTO> result = associadoService.buscarAssociados("João", null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("João Silva", result.getContent().get(0).getNome());

        verify(associadoRepository).findByNomeContainingIgnoreCaseOrCpfContaining(
                eq("João"), eq(null), eq(pageable));
        verify(associadoMapper).toDto(any(Associado.class));
    }

    @Test
    @DisplayName("Deve buscar associados por CPF")
    void buscarAssociadosPorCPF() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Associado> associados = List.of(associado);
        Page<Associado> associadosPage = new PageImpl<>(associados, pageable, 1);

        when(associadoRepository.findByNomeContainingIgnoreCaseOrCpfContaining(
                eq(null), eq("12345"), any(Pageable.class))).thenReturn(associadosPage);
        when(associadoMapper.toDto(any(Associado.class))).thenReturn(associadoDTO);

        // Act
        Page<AssociadoDTO> result = associadoService.buscarAssociados(null, "12345", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("12345678900", result.getContent().get(0).getCpf());

        verify(associadoRepository).findByNomeContainingIgnoreCaseOrCpfContaining(
                eq(null), eq("12345"), eq(pageable));
        verify(associadoMapper).toDto(any(Associado.class));
    }

    @Test
    @DisplayName("Deve buscar um associado por CPF")
    void buscarPorCpf() {
        // Arrange
        when(associadoRepository.findByCpf(anyString())).thenReturn(Optional.of(associado));
        when(associadoMapper.toDto(any(Associado.class))).thenReturn(associadoDTO);

        // Act
        AssociadoDTO result = associadoService.buscarPorCpf("12345678900");

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("João Silva", result.getNome());
        assertEquals("12345678900", result.getCpf());

        verify(associadoRepository).findByCpf("12345678900");
        verify(associadoMapper).toDto(associado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar associado com CPF inexistente")
    void buscarPorCpfInexistente() {
        // Arrange
        when(associadoRepository.findByCpf(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            associadoService.buscarPorCpf("99999999999");
        });

        verify(associadoRepository).findByCpf("99999999999");
        verify(associadoMapper, never()).toDto(any(Associado.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar associado com CPF vazio")
    void buscarPorCpfVazio() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            associadoService.buscarPorCpf("");
        });

        verify(associadoRepository, never()).findByCpf(anyString());
        verify(associadoMapper, never()).toDto(any(Associado.class));
    }
} 