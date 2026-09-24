package com.segurosbolivar.polizaapi.service;

import com.segurosbolivar.polizaapi.model.EstadoEnvio;
import com.segurosbolivar.polizaapi.model.EventoNotificacion;
import com.segurosbolivar.polizaapi.repository.EventoNotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lado "consumidor" del Outbox de notificaciones. En la arquitectura objetivo (Modulo 1,
 * seccion 5) esto corresponde a Servicio de Notificaciones, un deployable aparte; aqui
 * vive en el mismo proceso por el alcance acotado de esta prueba, y "envia" con un log en
 * vez de llamar a un proveedor real de correo/SMS.
 */
@Component
public class NotificacionPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificacionPublisher.class);

    private final EventoNotificacionRepository eventoNotificacionRepository;

    public NotificacionPublisher(EventoNotificacionRepository eventoNotificacionRepository) {
        this.eventoNotificacionRepository = eventoNotificacionRepository;
    }

    @Scheduled(fixedDelayString = "${notificacion.publicador.intervalo-ms:5000}")
    @Transactional
    public void publicarPendientes() {
        List<EventoNotificacion> pendientes = eventoNotificacionRepository.findByEstadoEnvio(EstadoEnvio.PENDIENTE);
        for (EventoNotificacion evento : pendientes) {
            log.info("[NOTIFICACION-MOCK] Enviando {} por {} para poliza {}",
                    evento.getTipoEvento(), evento.getCanal(), evento.getPolizaId());
            evento.marcarEnviado();
        }
    }
}
