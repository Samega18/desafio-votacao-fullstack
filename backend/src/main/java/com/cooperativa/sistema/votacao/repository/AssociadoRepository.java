package com.cooperativa.sistema.votacao.repository;

import com.cooperativa.sistema.votacao.domain.Associado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    /**
     * Find associates by name or CPF
     * 
     * @param nome Name to search for (can be partial)
     * @param cpf CPF to search for (can be partial)
     * @param pageable Pagination information
     * @return Page of associates matching the criteria
     */
    @Query("SELECT a FROM Associado a WHERE (:nome IS NULL OR LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND (:cpf IS NULL OR a.cpf LIKE CONCAT('%', :cpf, '%'))")
    Page<Associado> findByNomeContainingIgnoreCaseOrCpfContaining(
            @Param("nome") String nome, 
            @Param("cpf") String cpf, 
            Pageable pageable);
}