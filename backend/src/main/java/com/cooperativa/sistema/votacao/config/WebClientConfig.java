package com.cooperativa.sistema.votacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for the application
 */
@Configuration
public class WebClientConfig {
    
    /**
     * RestTemplate for making HTTP requests
     * Nome alterado para evitar conflito com o bean em RestTemplateConfig
     */
    @Bean(name = "webClientRestTemplate")
    public RestTemplate webClientRestTemplate() {
        return new RestTemplate();
    }
    
    /**
     * Configure CORS for the application
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")  // For development only, restrict in production
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}