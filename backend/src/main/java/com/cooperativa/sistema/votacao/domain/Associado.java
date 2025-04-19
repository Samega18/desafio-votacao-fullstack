package com.cooperativa.sistema.votacao.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a cooperative associate
 */
@Entity
@Table(name = "associado")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Associado {
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String cpf;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
    
    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
    }
}