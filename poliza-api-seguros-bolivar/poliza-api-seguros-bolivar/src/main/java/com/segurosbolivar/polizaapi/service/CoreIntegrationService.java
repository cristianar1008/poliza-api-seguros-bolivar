package com.segurosbolivar.polizaapi.service;

import com.segurosbolivar.polizaapi.dto.CoreEventoRequest;
import com.segurosbolivar.polizaapi.port.CoreNotificadorPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Adaptador hacia el "servicio agnostico de edicion" que en el enunciado se dispone a
 * traves de la capa media en WebLogic hacia el CORE transaccional legado. Aqui llama de
 * verdad a CoreMockController (POST /core-mock/evento), que hace las veces de esa capa
 * para efectos de esta prueba — el requisito 4 del caso exige que las acciones que
 * cambian estado *consuman* ese servicio, no que solo lo dejen registrado en logs.
 *
 * Es best-effort y no bloquea la operacion de negocio si el mock falla (solo lo loggea):
 * el patron real que reemplazaria esto (Outbox + reintentos + circuit breaker) queda
 * documentado como arquitectura objetivo en el Modulo 1, seccion 4.3, y no se implementa
 * aqui a proposito, para no ampliar el alcance de esta prueba.
 */
@Service
public class CoreIntegrationService implements CoreNotificadorPort {

    private static final Logger log = LoggerFactory.getLogger(CoreIntegrationService.class);

    private final RestClient restClient;
    private final String coreMockUrl;

    public CoreIntegrationService(RestClient.Builder restClientBuilder,
                                   @Value("${core.mock.url}") String coreMockUrl) {
        this.restClient = restClientBuilder.build();
        this.coreMockUrl = coreMockUrl;
    }

    @Override
    public void notificarEvento(String evento, Long polizaId) {
        try {
            restClient.post()
                    .uri(coreMockUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CoreEventoRequest(evento, polizaId))
                    .retrieve()
                    .toBodilessEntity();
            log.info("[CORE-ADAPTER] Evento '{}' de poliza {} enviado al CORE (mock)", evento, polizaId);
        } catch (Exception ex) {
            log.warn("[CORE-ADAPTER] No se pudo enviar el evento '{}' de poliza {} al CORE: {}",
                    evento, polizaId, ex.getMessage());
        }
    }
}
