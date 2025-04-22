package com.cooperativa.sistema.votacao.client;

import com.cooperativa.sistema.votacao.domain.StatusVotacao;
import com.cooperativa.sistema.votacao.dto.CPFValidationResponse;
import com.cooperativa.sistema.votacao.exception.AssociadoNaoPodeVotarException;
import com.cooperativa.sistema.votacao.exception.CPFInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CPFValidatorTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Random random;

    @InjectMocks
    private CPFValidator cpfValidator;

    private final String validatorUrl = "http://localhost:8080/api/cpf-validation";

    @BeforeEach
    void setUp() {
        // Usando um método privado para acessar o campo random privado
        setPrivateField();
    }

    @Test
    @DisplayName("Deve retornar que o associado pode votar quando o CPF é válido")
    void validarCPFValido() {
        // Arrange
        String cpf = "12345678900";
        
        // Configurar o mock para não retornar 0 (não será CPF inválido nem "não pode votar")
        when(random.nextInt(5)).thenReturn(1, 1);

        // Act
        StatusVotacao result = cpfValidator.validarCPF(cpf);

        // Assert
        assertEquals(StatusVotacao.ABLE_TO_VOTE, result);
        verify(random, times(2)).nextInt(5);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o associado não pode votar")
    void validarCPFAssociadoNaoPodeVotar() {
        // Arrange
        String cpf = "12345678900";
        
        // Primeiro verificar se o CPF é inválido (não), depois verificar se o associado pode votar (não pode)
        when(random.nextInt(5)).thenReturn(1, 0);

        // Act & Assert
        AssociadoNaoPodeVotarException exception = assertThrows(AssociadoNaoPodeVotarException.class, () -> {
            cpfValidator.validarCPF(cpf);
        });
        
        assertEquals("Associado não pode votar", exception.getMessage());
        verify(random, times(2)).nextInt(5);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorre erro HTTP 404")
    void validarCPFErroHTTP() {
        // Arrange
        String cpf = "12345678900";
        
        // Configurar o mock para não ser CPF inválido nem "não pode votar" no random
        when(random.nextInt(5)).thenReturn(1, 1);
        
        // Simular erro HTTP 404
        HttpClientErrorException notFoundException = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(restTemplate.getForEntity(anyString(), eq(CPFValidationResponse.class)))
            .thenThrow(notFoundException);

        // Act & Assert
        CPFInvalidoException exception = assertThrows(CPFInvalidoException.class, () -> {
            cpfValidator.validarCPF(cpf);
        });
        
        assertEquals("CPF inválido", exception.getMessage());
        verify(random, times(2)).nextInt(5);
        verify(restTemplate).getForEntity(anyString(), eq(CPFValidationResponse.class));
    }

    @Test
    @DisplayName("Deve lançar exceção original quando ocorre erro HTTP diferente de 404")
    void validarCPFErroHTTPOutro() {
        // Arrange
        String cpf = "12345678900";
        
        // Configurar o mock para não ser CPF inválido nem "não pode votar" no random
        when(random.nextInt(5)).thenReturn(1, 1);
        
        // Simular erro HTTP 500
        HttpClientErrorException internalServerError = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForEntity(anyString(), eq(CPFValidationResponse.class)))
            .thenThrow(internalServerError);

        // Act & Assert
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            cpfValidator.validarCPF(cpf);
        });
        
        assertSame(internalServerError, exception);
        verify(random, times(2)).nextInt(5);
        verify(restTemplate).getForEntity(anyString(), eq(CPFValidationResponse.class));
    }
    
    /**
     * Método para injetar o mock de Random no CPFValidator usando Reflection 
     */
    private void setPrivateField() {
        try {
            java.lang.reflect.Field randomField = CPFValidator.class.getDeclaredField("random");
            randomField.setAccessible(true);
            randomField.set(cpfValidator, random);
        } catch (Exception e) {
            fail("Não foi possível configurar o campo random: " + e.getMessage());
        }
    }
} 