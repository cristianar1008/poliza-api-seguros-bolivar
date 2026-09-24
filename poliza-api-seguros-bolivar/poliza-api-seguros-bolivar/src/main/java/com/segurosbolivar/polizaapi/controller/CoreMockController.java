package com.segurosbolivar.polizaapi.controller;

import com.segurosbolivar.polizaapi.dto.CoreEventoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Mock externo requerido por el enunciado: simula el endpoint del CORE.
 * Su unico proposito es registrar en logs que la operacion se intento enviar.
 */
@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/evento")
    public ResponseEntity<Map<String, String>> recibirEvento(@RequestBody CoreEventoRequest request) {
        log.info("[CORE-MOCK] Evento recibido: evento='{}' polizaId={}", request.evento(), request.polizaId());
        return ResponseEntity.ok(Map.of("status", "recibido"));
    }
}
