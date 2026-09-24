package com.segurosbolivar.polizaapi.controller;

import com.segurosbolivar.polizaapi.model.TipoDocumento;
import com.segurosbolivar.polizaapi.repository.TipoDocumentoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Catalogo de referencia (Modulo 1, seccion 3) — consumo tipico de un formulario del front. */
@RestController
@RequestMapping("/tipos-documento")
public class TipoDocumentoController {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoController(TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @GetMapping
    public ResponseEntity<List<TipoDocumento>> listar() {
        return ResponseEntity.ok(tipoDocumentoRepository.findAll());
    }
}
