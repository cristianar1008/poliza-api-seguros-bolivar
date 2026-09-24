package com.segurosbolivar.polizaapi.repository;

import com.segurosbolivar.polizaapi.model.RenovacionPoliza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RenovacionPolizaRepository extends JpaRepository<RenovacionPoliza, Long> {

    List<RenovacionPoliza> findByPolizaIdOrderByFechaRenovacionDesc(Long polizaId);
}
