package com.segurosbolivar.polizaapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Historial de renovaciones (Modulo 1, seccion 3): Poliza.renovar() muta el canon/prima
 * vigente in place; esta tabla guarda el "antes/despues" de cada renovacion para trazabilidad.
 */
@Entity
@Table(name = "renovaciones_poliza")
public class RenovacionPoliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id", nullable = false)
    @JsonIgnore
    private Poliza poliza;

    @Column(nullable = false)
    private LocalDate fechaRenovacion;

    @Column(nullable = false)
    private BigDecimal canonAnterior;

    @Column(nullable = false)
    private BigDecimal canonNuevo;

    @Column(nullable = false)
    private BigDecimal ipcAplicado;

    @Column(nullable = false)
    private BigDecimal primaAnterior;

    @Column(nullable = false)
    private BigDecimal primaNueva;

    protected RenovacionPoliza() {
        // JPA
    }

    public RenovacionPoliza(Poliza poliza, BigDecimal canonAnterior, BigDecimal canonNuevo,
                             BigDecimal ipcAplicado, BigDecimal primaAnterior, BigDecimal primaNueva) {
        this.poliza = poliza;
        this.fechaRenovacion = LocalDate.now();
        this.canonAnterior = canonAnterior;
        this.canonNuevo = canonNuevo;
        this.ipcAplicado = ipcAplicado;
        this.primaAnterior = primaAnterior;
        this.primaNueva = primaNueva;
    }

    public Long getId() {
        return id;
    }

    public Long getPolizaId() {
        return poliza.getId();
    }

    public LocalDate getFechaRenovacion() {
        return fechaRenovacion;
    }

    public BigDecimal getCanonAnterior() {
        return canonAnterior;
    }

    public BigDecimal getCanonNuevo() {
        return canonNuevo;
    }

    public BigDecimal getIpcAplicado() {
        return ipcAplicado;
    }

    public BigDecimal getPrimaAnterior() {
        return primaAnterior;
    }

    public BigDecimal getPrimaNueva() {
        return primaNueva;
    }
}
