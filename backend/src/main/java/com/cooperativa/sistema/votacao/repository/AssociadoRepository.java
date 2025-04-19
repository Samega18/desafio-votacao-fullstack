package com.cooperativa.sistema.votacao.repository;

import com.cooperativa.sistema.votacao.domain.Associado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Associado entity
 */
@Repository
public interface AssociadoRepository extends JpaRepository<Associado, String> {
    
    /**
     * Find associate by CPF
     * 
     * @param cpf CPF of the associate
     * @return Optional containing the associate if found
     */
    Optional<Associado> findByCpf(String cpf);
    
    /**
     * Check if an associate exists by CPF
     * 
     * @param cpf CPF of the associate
     * @return true if associate exists, false otherwise
     */
    boolean existsByCpf(String cpf);
}