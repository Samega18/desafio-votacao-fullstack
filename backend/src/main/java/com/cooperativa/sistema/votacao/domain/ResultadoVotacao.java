package com.cooperativa.sistema.votacao.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing the result of a voting session
 */
@Entity
@Table(name = "resultado_votacao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoVotacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_votacao_id", nullable = false, unique = true)
    private SessaoVotacao sessaoVotacao;
    
    @Column(nullable = false)
    private Integer totalVotos;
    
    @Column(nullable = false)
    private Integer votosSim;
    
    @Column(nullable = false)
    private Integer votosNao;
    
    @Column(nullable = false)
    private LocalDateTime dataApuracao;
    
    /**
     * Calculate percentage of SIM votes
     * @return percentage of SIM votes
     */
    public Double getPercentualSim() {
        if (totalVotos == 0) return 0.0;
        return (votosSim * 100.0) / totalVotos;
    }
    
    /**
     * Calculate percentage of NAO votes
     * @return percentage of NAO votes
     */
    public Double getPercentualNao() {
        if (totalVotos == 0) return 0.0;
        return (votosNao * 100.0) / totalVotos;
    }
    
    /**
     * Check if the agenda was approved
     * Simple majority: more SIM than NAO votes
     * @return true if approved, false otherwise
     */
    public Boolean isAprovado() {
        return votosSim > votosNao;
    }
    
    @PrePersist
    protected void onCreate() {
        dataApuracao = LocalDateTime.now();
    }
}