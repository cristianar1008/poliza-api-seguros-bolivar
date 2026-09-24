package com.segurosbolivar.polizaapi.model;

import jakarta.persistence.*;

/**
 * Se reutiliza para tomador (Poliza), y para asegurado/beneficiario (Riesgo) —
 * ver Modulo 1, seccion 3, sobre por que asegurado/beneficiario viven en Riesgo
 * y no en Poliza.
 */
@Entity
@Table(name = "terceros", uniqueConstraints = @UniqueConstraint(columnNames = {"tipo_documento_id", "numero_documento"}))
public class Tercero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_documento_id", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false)
    private String numeroDocumento;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPersona tipoPersona;

    private String correo;
    private String telefono;

    protected Tercero() {
        // JPA
    }

    public Tercero(TipoDocumento tipoDocumento, String numeroDocumento, String nombre,
                   TipoPersona tipoPersona, String correo, String telefono) {
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombre = nombre;
        this.tipoPersona = tipoPersona;
        this.correo = correo;
        this.telefono = telefono;
    }

    public Long getId() {
        return id;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }
}
