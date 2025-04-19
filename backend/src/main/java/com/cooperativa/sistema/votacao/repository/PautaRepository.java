package com.cooperativa.sistema.votacao.repository;

import com.cooperativa.sistema.votacao.domain.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Pauta entity
 */
@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {
    // Default methods from JpaRepository are sufficient for now
}