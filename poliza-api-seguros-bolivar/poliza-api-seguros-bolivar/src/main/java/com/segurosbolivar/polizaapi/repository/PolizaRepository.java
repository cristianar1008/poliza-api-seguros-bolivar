package com.segurosbolivar.polizaapi.repository;

import com.segurosbolivar.polizaapi.model.EstadoPoliza;
import com.segurosbolivar.polizaapi.model.Poliza;
import com.segurosbolivar.polizaapi.model.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    List<Poliza> findByTipoAndEstado(TipoPoliza tipo, EstadoPoliza estado);

    List<Poliza> findByTipo(TipoPoliza tipo);

    List<Poliza> findByEstado(EstadoPoliza estado);
}
