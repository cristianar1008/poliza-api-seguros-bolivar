package com.segurosbolivar.polizaapi.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    /** Valor del canon mensual de arrendamiento. */
    @Column(nullable = false)
    private BigDecimal valorCanon;

    /** Prima = valor canon mensual * numero de meses de vigencia. */
    @Column(nullable = false)
    private BigDecimal valorPrima;

    /** Unico rol de Tercero que vive en Poliza — asegurado/beneficiario viven en Riesgo (Modulo 1, seccion 3). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tomador_id", nullable = false)
    private Tercero tomador;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riesgo> riesgos = new ArrayList<>();

    protected Poliza() {
        // JPA
    }

    public Poliza(TipoPoliza tipo, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal valorCanon,
                  Tercero tomador) {
        this.tipo = tipo;
        this.estado = EstadoPoliza.ACTIVA;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.valorCanon = valorCanon;
        this.valorPrima = calcularPrima(valorCanon, fechaInicio, fechaFin);
        this.tomador = tomador;
    }

    public static BigDecimal calcularPrima(BigDecimal valorCanon, LocalDate desde, LocalDate hasta) {
        long meses = java.time.temporal.ChronoUnit.MONTHS.between(
                desde.withDayOfMonth(1), hasta.withDayOfMonth(1)) + 1;
        return valorCanon.multiply(BigDecimal.valueOf(meses));
    }

    public void renovar(BigDecimal ipc) {
        if (this.estado == EstadoPoliza.CANCELADA) {
            throw new IllegalStateException("No se puede renovar una poliza cancelada");
        }
        BigDecimal incremento = BigDecimal.ONE.add(ipc);
        this.valorCanon = this.valorCanon.multiply(incremento);
        long duracionMeses = java.time.temporal.ChronoUnit.MONTHS.between(
                this.fechaInicio.withDayOfMonth(1), this.fechaFin.withDayOfMonth(1)) + 1;
        this.fechaInicio = this.fechaFin.plusDays(1);
        this.fechaFin = this.fechaInicio.plusMonths(duracionMeses).minusDays(1);
        this.valorPrima = this.valorCanon.multiply(BigDecimal.valueOf(duracionMeses));
        this.estado = EstadoPoliza.RENOVADA;
    }

    public void cancelar() {
        this.estado = EstadoPoliza.CANCELADA;
        for (Riesgo riesgo : riesgos) {
            riesgo.cancelar();
        }
    }

    public void agregarRiesgo(Riesgo riesgo) {
        if (this.tipo != TipoPoliza.COLECTIVA) {
            throw new IllegalStateException("Solo las polizas colectivas admiten agregar riesgos");
        }
        riesgo.setPoliza(this);
        this.riesgos.add(riesgo);
    }

    /**
     * Asigna el unico riesgo permitido a una poliza INDIVIDUAL. Se usa solo durante
     * la creacion (a diferencia de agregarRiesgo, que es exclusivo de COLECTIVA y
     * queda expuesto via POST /polizas/{id}/riesgos). Aplica la regla de negocio
     * "una poliza individual solo puede tener 1 riesgo".
     */
    public void asignarRiesgoIndividual(Riesgo riesgo) {
        if (this.tipo != TipoPoliza.INDIVIDUAL) {
            throw new IllegalStateException("Este metodo solo aplica para polizas INDIVIDUAL");
        }
        if (!this.riesgos.isEmpty()) {
            throw new IllegalStateException("Una poliza individual solo puede tener 1 riesgo");
        }
        riesgo.setPoliza(this);
        this.riesgos.add(riesgo);
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public TipoPoliza getTipo() {
        return tipo;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public void setEstado(EstadoPoliza estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public BigDecimal getValorCanon() {
        return valorCanon;
    }

    public BigDecimal getValorPrima() {
        return valorPrima;
    }

    public Tercero getTomador() {
        return tomador;
    }

    public List<Riesgo> getRiesgos() {
        return riesgos;
    }
}
