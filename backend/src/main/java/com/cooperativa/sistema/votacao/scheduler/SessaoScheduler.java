package com.cooperativa.sistema.votacao.scheduler;

import com.cooperativa.sistema.votacao.domain.SessaoVotacao;
import com.cooperativa.sistema.votacao.repository.SessaoVotacaoRepository;
import com.cooperativa.sistema.votacao.service.SessaoVotacaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler for managing voting sessions
 * Responsible for closing expired sessions automatically
 */
@Component
@Slf4j
public class SessaoScheduler {

    private final SessaoVotacaoService sessaoVotacaoService;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    @Autowired
    public SessaoScheduler(@Lazy SessaoVotacaoService sessaoVotacaoService,
                          SessaoVotacaoRepository sessaoVotacaoRepository) {
        this.sessaoVotacaoService = sessaoVotacaoService;
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
    }

    /**
     * Schedule session closing based on session end time
     * This method would be called when a new session is created
     * 
     * @param sessao Session to schedule closing for
     */
    public void agendarFechamento(SessaoVotacao sessao) {
        log.info("Agendando fechamento da sessão {} para {}", sessao.getId(), sessao.getDataFechamento());
        // No need to explicitly schedule individual tasks since we have the batch job below
    }

    /**
     * Check for expired sessions every 10 seconds
     * This is a batch job that closes all expired sessions
     */
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void verificarSessoesExpiradas() {
        log.info("Verificando sessões expiradas");
        
        LocalDateTime now = LocalDateTime.now();
        List<SessaoVotacao> sessoesExpiradas = sessaoVotacaoRepository
                .findByEncerradaFalseAndDataFechamentoBefore(now);
        
        if (!sessoesExpiradas.isEmpty()) {
            log.info("Encontradas {} sessões expiradas para encerramento", sessoesExpiradas.size());
            
            for (SessaoVotacao sessao : sessoesExpiradas) {
                log.info("Encerrando automaticamente a sessão {}", sessao.getId());
                try {
                    sessaoVotacaoService.encerrarSessao(sessao.getId());
                } catch (Exception e) {
                    log.error("Erro ao encerrar sessão {}: {}", sessao.getId(), e.getMessage());
                }
            }
        }
    }
}