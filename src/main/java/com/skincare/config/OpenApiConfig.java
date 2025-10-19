package com.skincare.config; // Ganti dengan package config Anda

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Kelas Konfigurasi untuk kustomisasi OpenAPI (Swagger).
 * Kelas ini mendefinisikan skema keamanan global untuk otorisasi Bearer Token (JWT).
 */
@Configuration
public class OpenApiConfig {

    private final String port;
    public OpenApiConfig(@Value("${server.port}") String port) {
        this.port = port;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final String apiTitle = "KAI Test Kafka API";
        final String apiVersion = "1.0.0";


        Server localServer = new Server()
                .url("http://localhost:" + port + "/kai-test-kafka")
                .description("Skripsi API Local Development Server");

        Server deployServer = new Server()
                .url("http://43.157.209.107:30825/kai-test-kafka")
                .description("Skripsi API Deployment Server");

        return new OpenAPI()
                .addServersItem(localServer)
                .addServersItem(deployServer)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))

                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                .info(new Info()
                        .title(apiTitle)
                        .description(apiTitle)
                        .version(apiVersion));
    }
}

// trig