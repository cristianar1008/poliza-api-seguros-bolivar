package com.segurosbolivar.polizaapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Metadatos de la documentacion OpenAPI/Swagger y el esquema de seguridad, para que
 * Swagger UI muestre el boton "Authorize" y permita probar los endpoints enviando el
 * header x-api-key exigido por ApiKeyInterceptor.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "API de Gestion de Polizas",
                version = "v1",
                description = "Prueba tecnica Seguros Bolivar - Modulo 2. Gestion de polizas y riesgos "
                        + "de arrendamiento (Individual y Colectiva), renovacion, cancelacion y "
                        + "notificacion de eventos al CORE (mock)."
        ),
        security = @SecurityRequirement(name = "ApiKeyAuth")
)
@SecurityScheme(
        name = "ApiKeyAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "x-api-key"
)
@Configuration
public class OpenApiConfig {
}
