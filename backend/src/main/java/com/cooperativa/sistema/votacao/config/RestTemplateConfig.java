package com.cooperativa.sistema.votacao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuração do RestTemplate para chamadas HTTP externas
 */
@Configuration
public class RestTemplateConfig {

    @Value("${REST_CONNECT_TIMEOUT:5000}")
    private int connectTimeout;

    @Value("${REST_READ_TIMEOUT:5000}")
    private int readTimeout;

    /**
     * Configura o RestTemplate com timeouts configuráveis via variáveis de ambiente
     * 
     * @param builder RestTemplateBuilder injetado pelo Spring
     * @return RestTemplate configurado
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }
}