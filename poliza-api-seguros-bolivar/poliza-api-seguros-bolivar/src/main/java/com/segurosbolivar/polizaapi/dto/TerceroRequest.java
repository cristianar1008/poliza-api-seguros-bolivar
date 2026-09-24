package com.segurosbolivar.polizaapi.dto;

import com.segurosbolivar.polizaapi.model.TipoPersona;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TerceroRequest(
        @NotBlank String tipoDocumentoCodigo,
        @NotBlank String numeroDocumento,
        @NotBlank String nombre,
        @NotNull TipoPersona tipoPersona,
        String correo,
        String telefono
) {
}
