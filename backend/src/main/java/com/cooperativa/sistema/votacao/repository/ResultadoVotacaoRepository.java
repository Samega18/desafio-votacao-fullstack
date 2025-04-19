package com.cooperativa.sistema.votacao.repository;

import com.cooperativa.sistema.votacao.domain.ResultadoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ResultadoVotacao entity
 */
@Repository
public interface ResultadoVotacaoRepository extends JpaRepository<ResultadoVotacao, Long> {
    
    /**
     * Find result by session ID
     * 
     * @param sessaoId ID of the voting session
     * @return Optional containing the voting result if found
     */
    Optional<ResultadoVotacao> findBySessaoVotacaoId(Long sessaoId);
    
    /**
     * Check if a result exists for a session
     * 
     * @param sessaoId ID of the voting session
     * @return true if result exists, false otherwise
     */
    boolean existsBySessaoVotacaoId(Long sessaoId);
}