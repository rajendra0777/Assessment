package com.caprae.saasquatch.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "BearerAuth";
        final String apiKeySchemeName = "ApiKeyAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Caprae Capital - SaaSquatch LeadGen Pro API")
                        .version("1.0.0")
                        .description("Enterprise Real-Time B2B Lead Generation, AI Deal Sourcing, and Multi-Source Enrichment Engine engineered for Caprae Capital Partners ETA & Private Equity deal pipelines.")
                        .contact(new Contact()
                                .name("Caprae Capital Engineering Team")
                                .email("recruiting@capraecapital.com")
                                .url("https://capraecapital.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName).addList(apiKeySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        .addSecuritySchemes(apiKeySchemeName,
                                new SecurityScheme()
                                        .name("X-API-KEY")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)));
    }
}
