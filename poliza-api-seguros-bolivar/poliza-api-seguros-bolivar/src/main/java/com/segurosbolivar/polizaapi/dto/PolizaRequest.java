package com.segurosbolivar.polizaapi.dto;

import com.segurosbolivar.polizaapi.model.TipoPoliza;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolizaRequest(
        @NotNull TipoPoliza tipo,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        @NotNull BigDecimal valorCanon,
        @NotNull @Valid TerceroRequest tomador,
        /** Solo aplica (y es requerido) si tipo = INDIVIDUAL; ver Riesgo.asegurado/beneficiario. */
        @Valid TerceroRequest asegurado,
        @Valid TerceroRequest beneficiario,
        /** Solo aplica si tipo = INDIVIDUAL: descripcion del unico riesgo (inmueble) a asegurar. */
        String riesgoDescripcion
) {
}
