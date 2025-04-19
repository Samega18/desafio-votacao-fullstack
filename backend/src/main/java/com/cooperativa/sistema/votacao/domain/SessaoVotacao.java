package com.cooperativa.sistema.votacao.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a voting session for an agenda
 */
@Entity
@Table(name = "sessao_votacao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoVotacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;
    
    @Column(nullable = false)
    private LocalDateTime dataAbertura;
    
    @Column(nullable = false)
    private LocalDateTime dataFechamento;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean encerrada = false;
    
    @OneToMany(mappedBy = "sessaoVotacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Voto> votos = new ArrayList<>();
    
    @OneToOne(mappedBy = "sessaoVotacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ResultadoVotacao resultado;
    
    /**
     * Method to close a voting session
     */
    public void encerrarSessao() {
        this.encerrada = true;
    }
    
    /**
     * Method to check if a session is open
     * @return true if the session is still open, false otherwise
     */
    public boolean isAberta() {
        return !encerrada && 
               LocalDateTime.now().isAfter(dataAbertura) && 
               LocalDateTime.now().isBefore(dataFechamento);
    }
    
    /**
     * Method to check if a session has expired but is not yet closed
     * @return true if the session has expired but not closed, false otherwise
     */
    public boolean isExpiradaNaoEncerrada() {
        return !encerrada && LocalDateTime.now().isAfter(dataFechamento);
    }
    
    @PrePersist
    protected void onCreate() {
        if (dataAbertura == null) {
            dataAbertura = LocalDateTime.now();
        }
        
        // Default duration: 1 minute
        if (dataFechamento == null) {
            dataFechamento = dataAbertura.plusMinutes(1);
        }
    }
}