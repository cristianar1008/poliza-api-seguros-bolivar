package com.segurosbolivar.polizaapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Seguridad minima solicitada por el enunciado: exige el header "api-key" en cada
 * peticion. En un entorno productivo esto se reemplazaria por OAuth2/JWT validado
 * en el API Gateway (ver Modulo 1).
 */
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private final String expectedApiKey;

    public ApiKeyInterceptor(@Value("${security.api-key}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String apiKey = request.getHeader("api-key");
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Header 'api-key' invalido o ausente\"}");
            return false;
        }
        return true;
    }
}
