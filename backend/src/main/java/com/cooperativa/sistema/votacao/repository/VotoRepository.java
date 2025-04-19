package com.cooperativa.sistema.votacao.repository;

import com.cooperativa.sistema.votacao.domain.Voto;
import com.cooperativa.sistema.votacao.domain.OpcaoVoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Voto entity
 */
@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    
    /**
     * Check if an associate has already voted in a session
     * 
     * @param sessaoId ID of the voting session
     * @param idAssociado ID of the associate
     * @return true if the associate has voted, false otherwise
     */
    boolean existsBySessaoVotacaoIdAndIdAssociado(Long sessaoId, String idAssociado);
    
    /**
     * Find a vote by session ID and associate ID
     * 
     * @param sessaoId ID of the voting session
     * @param idAssociado ID of the associate
     * @return Optional containing the vote if found
     */
    Optional<Voto> findBySessaoVotacaoIdAndIdAssociado(Long sessaoId, String idAssociado);
    
    /**
     * Count votes by option for a specific voting session
     * 
     * @param sessaoId ID of the voting session
     * @param opcao Vote option (SIM/NAO)
     * @return Count of votes for the specified option
     */
    long countBySessaoVotacaoIdAndOpcao(Long sessaoId, OpcaoVoto opcao);
    
    /**
     * Count total votes for a specific voting session
     * 
     * @param sessaoId ID of the voting session
     * @return Count of total votes
     */
    long countBySessaoVotacaoId(Long sessaoId);
    
    /**
     * Get vote counts by option for a specific session
     * 
     * @param sessaoId ID of the voting session
     * @return Object array with vote option and count
     */
    @Query("SELECT v.opcao, COUNT(v) FROM Voto v WHERE v.sessaoVotacao.id = :sessaoId GROUP BY v.opcao")
    Object[][] countVotosBySessaoId(Long sessaoId);
}