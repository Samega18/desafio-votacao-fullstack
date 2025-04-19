package com.cooperativa.sistema.votacao.repository;

import com.cooperativa.sistema.votacao.domain.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for SessaoVotacao entity
 */
@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {
    
    /**
     * Find all voting sessions that have expired but not closed yet
     * 
     * @param now Current date and time
     * @return List of expired and not closed voting sessions
     */
    List<SessaoVotacao> findByEncerradaFalseAndDataFechamentoBefore(LocalDateTime now);
    
    /**
     * Find all voting sessions for a specific agenda
     * 
     * @param pautaId ID of the agenda
     * @return List of voting sessions for the agenda
     */
    List<SessaoVotacao> findByPautaId(Long pautaId);
    
    /**
     * Check if there's an open voting session for a specific agenda
     * 
     * @param pautaId ID of the agenda
     * @param now Current date and time
     * @return true if there's an open session, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SessaoVotacao s " +
           "WHERE s.pauta.id = :pautaId AND s.encerrada = false AND s.dataAbertura <= :now AND s.dataFechamento >= :now")
    boolean existsOpenSessionForPauta(Long pautaId, LocalDateTime now);
}