package com.segurosbolivar.polizaapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * En Spring Boot 4.1.1 con spring-boot-starter-webmvc (en lugar del clasico
 * spring-boot-starter-web), la auto-configuracion no esta registrando un bean
 * de RestClient.Builder por si sola. CoreIntegrationService lo necesita para
 * llamar al CORE (mock), asi que lo declaramos explicitamente aqui.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
