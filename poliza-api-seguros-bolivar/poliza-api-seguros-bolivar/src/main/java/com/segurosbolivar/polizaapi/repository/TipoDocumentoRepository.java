package com.segurosbolivar.polizaapi.repository;

import com.segurosbolivar.polizaapi.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {

    Optional<TipoDocumento> findByCodigo(String codigo);
}
