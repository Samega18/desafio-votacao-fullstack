package com.cooperativa.sistema.votacao.service;

import com.cooperativa.sistema.votacao.client.CPFValidator;
import com.cooperativa.sistema.votacao.domain.OpcaoVoto;
import com.cooperativa.sistema.votacao.domain.Pauta;
import com.cooperativa.sistema.votacao.domain.SessaoVotacao;
import com.cooperativa.sistema.votacao.domain.StatusVotacao;
import com.cooperativa.sistema.votacao.domain.Voto;
import com.cooperativa.sistema.votacao.dto.VotoDTO;
import com.cooperativa.sistema.votacao.dto.VotoRequest;
import com.cooperativa.sistema.votacao.exception.CPFInvalidoException;
import com.cooperativa.sistema.votacao.exception.ResourceNotFoundException;
import com.cooperativa.sistema.votacao.exception.SessaoEncerradaException;
import com.cooperativa.sistema.votacao.exception.VotoJaRealizadoException;
import com.cooperativa.sistema.votacao.mapper.VotoMapper;
import com.cooperativa.sistema.votacao.repository.SessaoVotacaoRepository;
import com.cooperativa.sistema.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private CPFValidator cpfValidator;

    @Mock
    private VotoMapper votoMapper;

    @InjectMocks
    private VotoService votoService;

    private SessaoVotacao sessaoAberta;
    private SessaoVotacao sessaoEncerrada;
    private Pauta pauta;
    private Voto voto;
    private VotoDTO votoDTO;
    private VotoRequest votoRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime agora = LocalDateTime.now();

        pauta = Pauta.builder()
                .id(1L)
                .titulo("Pauta de Teste")
                .dataCriacao(agora.minusDays(1))
                .build();

        sessaoAberta = SessaoVotacao.builder()
                .id(1L)
                .pauta(pauta)
                .dataAbertura(agora.minusMinutes(5))
                .dataFechamento(agora.plusMinutes(5))
                .encerrada(false)
                .build();

        sessaoEncerrada = SessaoVotacao.builder()
                .id(2L)
                .pauta(pauta)
                .dataAbertura(agora.minusMinutes(10))
                .dataFechamento(agora.minusMinutes(5))
                .encerrada(true)
                .build();

        voto = Voto.builder()
                .id(1L)
                .sessaoVotacao(sessaoAberta)
                .idAssociado("1")
                .opcao(OpcaoVoto.SIM)
                .dataHoraVoto(agora)
                .build();

        votoDTO = VotoDTO.builder()
                .id(1L)
                .idAssociado("1")
                .opcao(OpcaoVoto.SIM)
                .dataHoraVoto(agora)
                .build();

        votoRequest = new VotoRequest();
        votoRequest.setIdAssociado("1");
        votoRequest.setCpf("12345678900");
        votoRequest.setOpcao(OpcaoVoto.SIM);
    }

    @Test
    @DisplayName("Deve registrar um voto com sucesso")
    void registrarVoto() {
        // Arrange
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessaoAberta));
        when(votoRepository.existsBySessaoVotacaoIdAndIdAssociado(anyLong(), anyString())).thenReturn(false);
        when(cpfValidator.validarCPF(anyString())).thenReturn(StatusVotacao.ABLE_TO_VOTE);
        when(votoMapper.toEntity(any(VotoRequest.class), any(SessaoVotacao.class))).thenReturn(voto);
        when(votoRepository.save(any(Voto.class))).thenReturn(voto);
        when(votoMapper.toDto(any(Voto.class))).thenReturn(votoDTO);

        // Act
        VotoDTO result = votoService.registrarVoto(1L, votoRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("1", result.getIdAssociado());
        assertEquals(OpcaoVoto.SIM, result.getOpcao());

        verify(sessaoRepository).findById(1L);
        verify(votoRepository).existsBySessaoVotacaoIdAndIdAssociado(1L, "1");
        verify(cpfValidator).validarCPF("12345678900");
        verify(votoMapper).toEntity(votoRequest, sessaoAberta);
        verify(votoRepository).save(voto);
        verify(votoMapper).toDto(voto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar voto em sessão inexistente")
    void registrarVotoSessaoInexistente() {
        // Arrange
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            votoService.registrarVoto(99L, votoRequest);
        });

        assertTrue(exception.getMessage().contains("Sessão de votação não encontrada"));
        verify(sessaoRepository).findById(99L);
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar voto em sessão encerrada")
    void registrarVotoSessaoEncerrada() {
        // Arrange
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessaoEncerrada));

        // Act & Assert
        SessaoEncerradaException exception = assertThrows(SessaoEncerradaException.class, () -> {
            votoService.registrarVoto(2L, votoRequest);
        });

        assertTrue(exception.getMessage().contains("Sessão de votação não está aberta"));
        verify(sessaoRepository).findById(2L);
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar voto duplicado")
    void registrarVotoDuplicado() {
        // Arrange
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessaoAberta));
        when(votoRepository.existsBySessaoVotacaoIdAndIdAssociado(anyLong(), anyString())).thenReturn(true);

        // Act & Assert
        VotoJaRealizadoException exception = assertThrows(VotoJaRealizadoException.class, () -> {
            votoService.registrarVoto(1L, votoRequest);
        });

        assertTrue(exception.getMessage().contains("Associado já votou nesta sessão"));
        verify(sessaoRepository).findById(1L);
        verify(votoRepository).existsBySessaoVotacaoIdAndIdAssociado(1L, "1");
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar voto com CPF inválido")
    void registrarVotoCPFInvalido() {
        // Arrange
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessaoAberta));
        when(votoRepository.existsBySessaoVotacaoIdAndIdAssociado(anyLong(), anyString())).thenReturn(false);
        doThrow(new CPFInvalidoException("CPF inválido")).when(cpfValidator).validarCPF(anyString());

        // Act & Assert
        CPFInvalidoException exception = assertThrows(CPFInvalidoException.class, () -> {
            votoService.registrarVoto(1L, votoRequest);
        });

        assertTrue(exception.getMessage().contains("CPF inválido"));
        verify(sessaoRepository).findById(1L);
        verify(votoRepository).existsBySessaoVotacaoIdAndIdAssociado(1L, "1");
        verify(cpfValidator).validarCPF("12345678900");
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer violação de integridade ao salvar")
    void registrarVotoDataIntegrityViolation() {
        // Arrange
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessaoAberta));
        when(votoRepository.existsBySessaoVotacaoIdAndIdAssociado(anyLong(), anyString())).thenReturn(false);
        when(cpfValidator.validarCPF(anyString())).thenReturn(StatusVotacao.ABLE_TO_VOTE);
        when(votoMapper.toEntity(any(VotoRequest.class), any(SessaoVotacao.class))).thenReturn(voto);
        when(votoRepository.save(any(Voto.class))).thenThrow(new DataIntegrityViolationException("Erro de integridade"));

        // Act & Assert
        VotoJaRealizadoException exception = assertThrows(VotoJaRealizadoException.class, () -> {
            votoService.registrarVoto(1L, votoRequest);
        });

        assertTrue(exception.getMessage().contains("Associado já votou nesta sessão"));
        verify(sessaoRepository).findById(1L);
        verify(votoRepository).existsBySessaoVotacaoIdAndIdAssociado(1L, "1");
        verify(cpfValidator).validarCPF("12345678900");
        verify(votoMapper).toEntity(votoRequest, sessaoAberta);
        verify(votoRepository).save(voto);
    }
} 