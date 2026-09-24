package com.segurosbolivar.polizaapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RiesgoRequest(
        @NotBlank String descripcion,
        @NotNull @Valid TerceroRequest asegurado,
        @NotNull @Valid TerceroRequest beneficiario
) {
}
