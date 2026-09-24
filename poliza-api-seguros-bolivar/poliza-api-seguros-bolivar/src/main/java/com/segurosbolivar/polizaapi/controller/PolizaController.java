package com.segurosbolivar.polizaapi.controller;

import com.segurosbolivar.polizaapi.dto.PolizaRequest;
import com.segurosbolivar.polizaapi.dto.RiesgoRequest;
import com.segurosbolivar.polizaapi.model.EstadoPoliza;
import com.segurosbolivar.polizaapi.model.EventoNotificacion;
import com.segurosbolivar.polizaapi.model.Poliza;
import com.segurosbolivar.polizaapi.model.RenovacionPoliza;
import com.segurosbolivar.polizaapi.model.Riesgo;
import com.segurosbolivar.polizaapi.model.TipoPoliza;
import com.segurosbolivar.polizaapi.service.PolizaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @PostMapping
    public ResponseEntity<Poliza> crear(@RequestBody @Valid PolizaRequest request) {
        Poliza poliza = polizaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(poliza);
    }

    @GetMapping
    public ResponseEntity<List<Poliza>> listar(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        return ResponseEntity.ok(polizaService.listar(tipo, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poliza> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.obtener(id));
    }

    @GetMapping("/{id}/riesgos")
    public ResponseEntity<List<Riesgo>> listarRiesgos(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.listarRiesgos(id));
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<Poliza> renovar(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.renovar(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Poliza> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.cancelar(id));
    }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<Riesgo> agregarRiesgo(@PathVariable Long id, @RequestBody @Valid RiesgoRequest request) {
        Riesgo riesgo = polizaService.agregarRiesgo(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(riesgo);
    }

    @GetMapping("/{id}/renovaciones")
    public ResponseEntity<List<RenovacionPoliza>> listarRenovaciones(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.listarRenovaciones(id));
    }

    @GetMapping("/{id}/notificaciones")
    public ResponseEntity<List<EventoNotificacion>> listarNotificaciones(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.listarNotificaciones(id));
    }
}
