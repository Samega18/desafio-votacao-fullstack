package com.cooperativa.sistema.votacao.service;

import com.cooperativa.sistema.votacao.domain.Pauta;
import com.cooperativa.sistema.votacao.dto.PautaDTO;
import com.cooperativa.sistema.votacao.dto.PautaRequest;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.mapper.PautaMapper;
import com.cooperativa.sistema.votacao.repository.PautaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private PautaMapper pautaMapper;

    @InjectMocks
    private PautaService pautaService;

    private Pauta pauta;
    private PautaDTO pautaDTO;
    private PautaRequest pautaRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        pauta = Pauta.builder()
                .id(1L)
                .titulo("Pauta de Teste")
                .descricao("Descrição da pauta de teste")
                .dataCriacao(LocalDateTime.now())
                .build();

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
    void criarPauta() {
        // Arrange
        when(pautaMapper.toEntity(any(PautaRequest.class))).thenReturn(pauta);
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);
        when(pautaMapper.toDto(any(Pauta.class))).thenReturn(pautaDTO);

        // Act
        PautaDTO result = pautaService.criarPauta(pautaRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pauta de Teste", result.getTitulo());
        assertEquals("Descrição da pauta de teste", result.getDescricao());

        verify(pautaMapper).toEntity(pautaRequest);
        verify(pautaRepository).save(pauta);
        verify(pautaMapper).toDto(pauta);
    }

    @Test
    @DisplayName("Deve listar todas as pautas com paginação")
    void listarPautas() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Pauta> pautas = Arrays.asList(
                pauta,
                Pauta.builder()
                        .id(2L)
                        .titulo("Outra Pauta")
                        .descricao("Descrição da outra pauta")
                        .dataCriacao(LocalDateTime.now())
                        .build()
        );
        Page<Pauta> pautasPage = new PageImpl<>(pautas, pageable, 2);

        when(pautaRepository.findAll(any(Pageable.class))).thenReturn(pautasPage);
        when(pautaMapper.toDto(pauta)).thenReturn(pautaDTO);
        when(pautaMapper.toDto(pautas.get(1))).thenReturn(
                PautaDTO.builder()
                        .id(2L)
                        .titulo("Outra Pauta")
                        .descricao("Descrição da outra pauta")
                        .dataCriacao(LocalDateTime.now())
                        .build()
        );

        // Act
        Page<PautaDTO> result = pautaService.listarPautas(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Pauta de Teste", result.getContent().get(0).getTitulo());
        assertEquals("Outra Pauta", result.getContent().get(1).getTitulo());

        verify(pautaRepository).findAll(pageable);
        verify(pautaMapper, times(2)).toDto(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve obter uma pauta específica pelo ID")
    void obterPauta() {
        // Arrange
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(pautaMapper.toDto(any(Pauta.class))).thenReturn(pautaDTO);

        // Act
        PautaDTO result = pautaService.obterPauta(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pauta de Teste", result.getTitulo());
        assertEquals("Descrição da pauta de teste", result.getDescricao());

        verify(pautaRepository).findById(1L);
        verify(pautaMapper).toDto(pauta);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pauta com ID inexistente")
    void obterPautaInexistente() {
        // Arrange
        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            pautaService.obterPauta(99L);
        });

        assertTrue(exception.getMessage().contains("Pauta não encontrada"));
        verify(pautaRepository).findById(99L);
        verify(pautaMapper, never()).toDto(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve obter uma entidade Pauta pelo ID")
    void obterPautaEntity() {
        // Arrange
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        // Act
        Pauta result = pautaService.obterPautaEntity(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pauta de Teste", result.getTitulo());
        assertEquals("Descrição da pauta de teste", result.getDescricao());

        verify(pautaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar entidade Pauta com ID inexistente")
    void obterPautaEntityInexistente() {
        // Arrange
        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            pautaService.obterPautaEntity(99L);
        });

        assertTrue(exception.getMessage().contains("Pauta não encontrada"));
        verify(pautaRepository).findById(99L);
    }
} 