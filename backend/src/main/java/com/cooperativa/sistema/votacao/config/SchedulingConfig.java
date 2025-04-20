package com.cooperativa.sistema.votacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration to enable task scheduling in Spring Boot
 * This configuration is required for methods annotated with @Scheduled to work.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

}