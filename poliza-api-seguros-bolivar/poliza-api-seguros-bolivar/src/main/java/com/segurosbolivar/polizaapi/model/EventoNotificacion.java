package com.segurosbolivar.polizaapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Tabla tipo Outbox (Modulo 1, seccion 3): vive en la base de datos de Servicio de
 * Pólizas (el productor), no en la de Servicio de Notificaciones, porque el patron
 * Outbox exige escribirla en la misma transaccion que el cambio de negocio.
 * NotificacionPublisher hace las veces del consumidor (Servicio de Notificaciones)
 * dentro de este mismo deployable, por el alcance acotado de esta prueba.
 */
@Entity
@Table(name = "eventos_notificacion")
public class EventoNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id", nullable = false)
    @JsonIgnore
    private Poliza poliza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEventoNotificacion tipoEvento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalNotificacion canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEnvio estadoEnvio;

    @Column(nullable = false)
    private LocalDateTime fecha;

    protected EventoNotificacion() {
        // JPA
    }

    public EventoNotificacion(Poliza poliza, TipoEventoNotificacion tipoEvento, CanalNotificacion canal) {
        this.poliza = poliza;
        this.tipoEvento = tipoEvento;
        this.canal = canal;
        this.estadoEnvio = EstadoEnvio.PENDIENTE;
        this.fecha = LocalDateTime.now();
    }

    public void marcarEnviado() {
        this.estadoEnvio = EstadoEnvio.ENVIADO;
    }

    public void marcarFallido() {
        this.estadoEnvio = EstadoEnvio.FALLIDO;
    }

    public Long getId() {
        return id;
    }

    public Long getPolizaId() {
        return poliza.getId();
    }

    public TipoEventoNotificacion getTipoEvento() {
        return tipoEvento;
    }

    public CanalNotificacion getCanal() {
        return canal;
    }

    public EstadoEnvio getEstadoEnvio() {
        return estadoEnvio;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
