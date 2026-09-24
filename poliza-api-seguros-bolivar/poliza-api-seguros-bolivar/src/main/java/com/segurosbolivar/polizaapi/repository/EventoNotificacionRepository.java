package com.segurosbolivar.polizaapi.repository;

import com.segurosbolivar.polizaapi.model.EstadoEnvio;
import com.segurosbolivar.polizaapi.model.EventoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoNotificacionRepository extends JpaRepository<EventoNotificacion, Long> {

    List<EventoNotificacion> findByEstadoEnvio(EstadoEnvio estadoEnvio);

    List<EventoNotificacion> findByPolizaIdOrderByFechaDesc(Long polizaId);
}
