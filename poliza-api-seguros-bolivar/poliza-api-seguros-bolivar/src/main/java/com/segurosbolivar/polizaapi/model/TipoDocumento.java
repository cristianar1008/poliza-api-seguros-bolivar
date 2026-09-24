package com.segurosbolivar.polizaapi.model;

import jakarta.persistence.*;

/**
 * Catalogo de referencia (Modulo 1, seccion 3): a diferencia de TipoPoliza/EstadoPoliza,
 * ningun comportamiento de negocio cambia segun su valor, y al integrarse con el CORE
 * legado es razonable necesitar mapear este codigo al que use el CORE. Por eso es la
 * unica tabla de catalogo del modelo; el resto de "tipo"/"estado" quedan como enum.
 */
@Entity
@Table(name = "tipos_documento")
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    protected TipoDocumento() {
        // JPA
    }

    public TipoDocumento(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
}
