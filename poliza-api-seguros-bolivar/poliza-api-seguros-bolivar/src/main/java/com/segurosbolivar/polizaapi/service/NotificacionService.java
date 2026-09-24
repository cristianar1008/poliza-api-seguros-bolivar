package com.segurosbolivar.polizaapi.service;

import com.segurosbolivar.polizaapi.model.CanalNotificacion;
import com.segurosbolivar.polizaapi.model.EventoNotificacion;
import com.segurosbolivar.polizaapi.model.Poliza;
import com.segurosbolivar.polizaapi.model.TipoEventoNotificacion;
import com.segurosbolivar.polizaapi.repository.EventoNotificacionRepository;
import org.springframework.stereotype.Service;

/**
 * Lado "productor" del Outbox de notificaciones (Modulo 1, seccion 2.3): registra la
 * intencion de notificar en la misma transaccion del cambio de negocio (PolizaService
 * llama esto dentro de su propio @Transactional). NotificacionPublisher es el consumidor.
 */
@Service
public class NotificacionService {

    private final EventoNotificacionRepository eventoNotificacionRepository;

    public NotificacionService(EventoNotificacionRepository eventoNotificacionRepository) {
        this.eventoNotificacionRepository = eventoNotificacionRepository;
    }

    public void registrarEvento(Poliza poliza, TipoEventoNotificacion tipoEvento) {
        eventoNotificacionRepository.save(new EventoNotificacion(poliza, tipoEvento, CanalNotificacion.EMAIL));
    }
}
