package com.cooperativa.sistema.votacao.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration for OpenAPI documentation
 */
@Configuration
public class OpenApiConfig {

    @Value("${api.version}")
    private String apiVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Votação para Cooperativas")
                        .version(apiVersion)
                        .description("API para gerenciar votações em assembleias de cooperativas")
                        .termsOfService("https://www.example.com/terms")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0"))
                        .contact(new Contact()
                                .name("Suporte")
                                .email("suporte@cooperativa.com")
                                .url("https://www.cooperativa.com/suporte")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desenvolvimento"),
                        new Server()
                                .url("https://api.cooperativa.com")
                                .description("Servidor de produção")))
                .tags(Arrays.asList(
                        createTag("pautas", "Gerenciamento de pautas para votação"),
                        createTag("sessoes", "Gerenciamento de sessões de votação"),
                        createTag("votos", "Registro e contabilização de votos"),
                        createTag("associados", "Gerenciamento de associados")));
    }

    private Tag createTag(String name, String description) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);
        return tag;
    }
}