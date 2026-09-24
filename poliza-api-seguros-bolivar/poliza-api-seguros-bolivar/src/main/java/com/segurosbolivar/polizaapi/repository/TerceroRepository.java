package com.segurosbolivar.polizaapi.repository;

import com.segurosbolivar.polizaapi.model.Tercero;
import com.segurosbolivar.polizaapi.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TerceroRepository extends JpaRepository<Tercero, Long> {

    Optional<Tercero> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
}
