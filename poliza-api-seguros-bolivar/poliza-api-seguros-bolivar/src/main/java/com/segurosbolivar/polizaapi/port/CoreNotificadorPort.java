package com.segurosbolivar.polizaapi.port;

/**
 * Puerto (Arquitectura Hexagonal, Modulo 1 seccion 2.1): el dominio de Polizas/Riesgos
 * conoce esta abstraccion, no como ni con que protocolo se notifica al CORE. La
 * implementacion concreta (adaptador) vive en el paquete "service" y decide el detalle
 * tecnico (HTTP, mensajeria, etc.) sin que PolizaService/RiesgoService dependan de eso.
 */
public interface CoreNotificadorPort {

    void notificarEvento(String evento, Long polizaId);
}
