package com.segurosbolivar.polizaapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "riesgos")
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    @JsonIgnore // evita el ciclo Poliza -> riesgos -> Riesgo -> poliza al serializar
    private Poliza poliza;

    @Column(nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRiesgo estado;

    /** Asegurado y beneficiario viven aqui, no en Poliza (Modulo 1, seccion 3). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asegurado_id", nullable = false)
    private Tercero asegurado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beneficiario_id", nullable = false)
    private Tercero beneficiario;

    protected Riesgo() {
        // JPA
    }

    public Riesgo(String descripcion, Tercero asegurado, Tercero beneficiario) {
        this.descripcion = descripcion;
        this.estado = EstadoRiesgo.ACTIVO;
        this.asegurado = asegurado;
        this.beneficiario = beneficiario;
    }

    public void cancelar() {
        this.estado = EstadoRiesgo.CANCELADO;
    }

    public Long getId() {
        return id;
    }

    public Poliza getPoliza() {
        return poliza;
    }

    public void setPoliza(Poliza poliza) {
        this.poliza = poliza;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoRiesgo getEstado() {
        return estado;
    }

    public Tercero getAsegurado() {
        return asegurado;
    }

    public Tercero getBeneficiario() {
        return beneficiario;
    }
}
