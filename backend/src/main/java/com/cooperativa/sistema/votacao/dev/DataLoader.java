package com.cooperativa.sistema.votacao.dev;

import com.cooperativa.sistema.votacao.domain.Associado;
import com.cooperativa.sistema.votacao.domain.Pauta;
import com.cooperativa.sistema.votacao.domain.SessaoVotacao;
import com.cooperativa.sistema.votacao.dto.PautaRequest;
import com.cooperativa.sistema.votacao.dto.SessaoRequest;
import com.cooperativa.sistema.votacao.dto.VotoRequest;
import com.cooperativa.sistema.votacao.repository.AssociadoRepository;
import com.cooperativa.sistema.votacao.repository.PautaRepository;
import com.cooperativa.sistema.votacao.service.AssociadoService;
import com.cooperativa.sistema.votacao.service.PautaService;
import com.cooperativa.sistema.votacao.service.SessaoVotacaoService;
import com.cooperativa.sistema.votacao.service.VotoService;
import com.cooperativa.sistema.votacao.domain.OpcaoVoto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Loader for development environment
 * This class will populate the database with test data when the application starts
 * Only enabled in 'dev' profile
 */
@Component
@Profile("dev")
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoService;
    private final VotoService votoService;
    private final AssociadoService associadoService;
    private final PautaRepository pautaRepository;
    private final AssociadoRepository associadoRepository;

    @Autowired
    public DataLoader(
            PautaService pautaService,
            SessaoVotacaoService sessaoService,
            VotoService votoService,
            AssociadoService associadoService,
            PautaRepository pautaRepository,
            AssociadoRepository associadoRepository) {
        this.pautaService = pautaService;
        this.sessaoService = sessaoService;
        this.votoService = votoService;
        this.associadoService = associadoService;
        this.pautaRepository = pautaRepository;
        this.associadoRepository = associadoRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Loading development data...");

        // Create associates
        createAssociados();
        
        // Create pautas
        createPautas();
        
        // Create sample voting session with votes
        createSampleVotingSession();
        
        log.info("Development data loaded successfully");
    }

    private void createAssociados() {
        log.info("Creating sample associates...");
        
        // Create 5 associates if they don't exist
        if (associadoRepository.count() == 0) {
            createAssociado("12345678901", "João Silva");
            createAssociado("23456789012", "Maria Santos");
            createAssociado("34567890123", "Pedro Oliveira");
            createAssociado("45678901234", "Ana Costa");
            createAssociado("56789012345", "Lucas Pereira");
        }
    }

    private void createAssociado(String cpf, String nome) {
        try {
            Associado associado = new Associado();
            associado.setId(UUID.randomUUID().toString());
            associado.setCpf(cpf);
            associado.setNome(nome);
            associado.setAtivo(true);
            associado.setDataCadastro(LocalDateTime.now());
            associadoRepository.save(associado);
            log.info("Created associate: {}", nome);
        } catch (Exception e) {
            log.error("Error creating associate {}: {}", nome, e.getMessage());
        }
    }

    private void createPautas() {
        log.info("Creating sample pautas...");
        
        // Create 3 pautas if they don't exist
        if (pautaRepository.count() == 0) {
            createPauta("Aumento da taxa de condomínio", 
                    "Proposta para aumentar a taxa de condomínio em 10% a partir do próximo mês");
            
            createPauta("Reforma da área de lazer", 
                    "Proposta para reformar a área de lazer do condomínio, incluindo piscina e churrasqueira");
            
            createPauta("Instalação de câmeras de segurança", 
                    "Proposta para instalar câmeras de segurança em todas as áreas comuns do condomínio");
        }
    }

    private void createPauta(String titulo, String descricao) {
        try {
            PautaRequest request = new PautaRequest();
            request.setTitulo(titulo);
            request.setDescricao(descricao);
            pautaService.criarPauta(request);
            log.info("Created pauta: {}", titulo);
        } catch (Exception e) {
            log.error("Error creating pauta {}: {}", titulo, e.getMessage());
        }
    }

    private void createSampleVotingSession() {
        log.info("Creating sample voting session with votes...");
        
        try {
            // Get the first pauta
            Pauta pauta = pautaRepository.findAll().stream().findFirst().orElse(null);
            if (pauta == null) {
                log.error("No pautas found for creating sample voting session");
                return;
            }
            
            // Create a session that lasts 5 minutes
            SessaoRequest sessaoRequest = new SessaoRequest();
            sessaoRequest.setDuracaoMinutos(5);
            var sessaoDTO = sessaoService.abrirSessao(pauta.getId(), sessaoRequest);
            log.info("Created voting session for pauta: {}", pauta.getTitulo());
            
            // Get all associates
            var associados = associadoRepository.findAll();
            
            // Register some votes
            int count = 0;
            for (Associado associado : associados) {
                try {
                    VotoRequest votoRequest = new VotoRequest();
                    votoRequest.setIdAssociado(associado.getId());
                    votoRequest.setCpf(associado.getCpf());
                    
                    // Alternate between SIM and NAO votes
                    votoRequest.setOpcao(count % 2 == 0 ? OpcaoVoto.SIM : OpcaoVoto.NAO);
                    
                    votoService.registrarVoto(sessaoDTO.getId(), votoRequest);
                    log.info("Registered vote for associate: {}", associado.getNome());
                    count++;
                } catch (Exception e) {
                    log.error("Error registering vote for associate {}: {}", 
                            associado.getNome(), e.getMessage());
                }
            }
            
            log.info("Sample voting session created successfully with {} votes", count);
        } catch (Exception e) {
            log.error("Error creating sample voting session: {}", e.getMessage());
        }
    }
}