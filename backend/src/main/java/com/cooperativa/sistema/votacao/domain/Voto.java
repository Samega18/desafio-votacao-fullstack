package com.cooperativa.sistema.votacao.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a vote in a voting session
 */
@Entity
@Table(name = "voto", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"sessao_votacao_id", "id_associado"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_associado", nullable = false)
    private String idAssociado;
    
    @Column(name = "cpf_associado", nullable = false)
    private String cpfAssociado;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpcaoVoto opcao;
    
    @Column(nullable = false)
    private LocalDateTime dataHoraVoto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_votacao_id", nullable = false)
    private SessaoVotacao sessaoVotacao;
    
    @PrePersist
    protected void onCreate() {
        dataHoraVoto = LocalDateTime.now();
    }
}