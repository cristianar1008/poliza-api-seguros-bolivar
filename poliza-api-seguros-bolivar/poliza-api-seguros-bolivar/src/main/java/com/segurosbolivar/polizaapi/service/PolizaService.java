package com.segurosbolivar.polizaapi.service;

import com.segurosbolivar.polizaapi.dto.PolizaRequest;
import com.segurosbolivar.polizaapi.dto.RiesgoRequest;
import com.segurosbolivar.polizaapi.exception.BusinessException;
import com.segurosbolivar.polizaapi.model.*;
import com.segurosbolivar.polizaapi.port.CoreNotificadorPort;
import com.segurosbolivar.polizaapi.repository.EventoNotificacionRepository;
import com.segurosbolivar.polizaapi.repository.PolizaRepository;
import com.segurosbolivar.polizaapi.repository.RenovacionPolizaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Capa de servicio: concentra las reglas de negocio de polizas y riesgos,
 * manteniendo el controlador y el repositorio libres de logica de dominio.
 */
@Service
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final BigDecimal ipc;
    private final CoreNotificadorPort coreIntegrationService;
    private final TerceroService terceroService;
    private final NotificacionService notificacionService;
    private final RenovacionPolizaRepository renovacionPolizaRepository;
    private final EventoNotificacionRepository eventoNotificacionRepository;

    public PolizaService(PolizaRepository polizaRepository,
                          @Value("${poliza.renovacion.ipc}") BigDecimal ipc,
                          CoreNotificadorPort coreIntegrationService,
                          TerceroService terceroService,
                          NotificacionService notificacionService,
                          RenovacionPolizaRepository renovacionPolizaRepository,
                          EventoNotificacionRepository eventoNotificacionRepository) {
        this.polizaRepository = polizaRepository;
        this.ipc = ipc;
        this.coreIntegrationService = coreIntegrationService;
        this.terceroService = terceroService;
        this.notificacionService = notificacionService;
        this.renovacionPolizaRepository = renovacionPolizaRepository;
        this.eventoNotificacionRepository = eventoNotificacionRepository;
    }

    @Transactional
    public Poliza crear(PolizaRequest request) {
        Tercero tomador = terceroService.obtenerOCrear(request.tomador());
        Poliza poliza = new Poliza(request.tipo(), request.fechaInicio(), request.fechaFin(),
                request.valorCanon(), tomador);

        if (request.tipo() == TipoPoliza.INDIVIDUAL) {
            if (request.asegurado() == null || request.beneficiario() == null || request.riesgoDescripcion() == null) {
                throw new BusinessException(
                        "Una poliza INDIVIDUAL requiere asegurado, beneficiario y la descripcion del riesgo");
            }
            Tercero asegurado = terceroService.obtenerOCrear(request.asegurado());
            Tercero beneficiario = terceroService.obtenerOCrear(request.beneficiario());
            Riesgo riesgo = new Riesgo(request.riesgoDescripcion(), asegurado, beneficiario);
            poliza.asignarRiesgoIndividual(riesgo);
        }

        Poliza guardada = polizaRepository.save(poliza);
        coreIntegrationService.notificarEvento("CREACION", guardada.getId());
        notificacionService.registrarEvento(guardada, TipoEventoNotificacion.CREACION);
        return guardada;
    }

    public List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado) {
        if (tipo != null && estado != null) {
            return polizaRepository.findByTipoAndEstado(tipo, estado);
        }
        if (tipo != null) {
            return polizaRepository.findByTipo(tipo);
        }
        if (estado != null) {
            return polizaRepository.findByEstado(estado);
        }
        return polizaRepository.findAll();
    }

    public Poliza obtener(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Poliza " + id + " no encontrada"));
    }

    public List<Riesgo> listarRiesgos(Long polizaId) {
        return obtener(polizaId).getRiesgos();
    }

    public List<RenovacionPoliza> listarRenovaciones(Long polizaId) {
        obtener(polizaId); // valida existencia
        return renovacionPolizaRepository.findByPolizaIdOrderByFechaRenovacionDesc(polizaId);
    }

    public List<EventoNotificacion> listarNotificaciones(Long polizaId) {
        obtener(polizaId); // valida existencia
        return eventoNotificacionRepository.findByPolizaIdOrderByFechaDesc(polizaId);
    }

    @Transactional
    public Poliza renovar(Long id) {
        Poliza poliza = obtener(id);
        BigDecimal canonAnterior = poliza.getValorCanon();
        BigDecimal primaAnterior = poliza.getValorPrima();

        poliza.renovar(ipc); // valida internamente que no este cancelada

        renovacionPolizaRepository.save(new RenovacionPoliza(
                poliza, canonAnterior, poliza.getValorCanon(), ipc, primaAnterior, poliza.getValorPrima()));
        coreIntegrationService.notificarEvento("RENOVACION", poliza.getId());
        notificacionService.registrarEvento(poliza, TipoEventoNotificacion.RENOVACION);
        return poliza;
    }

    @Transactional
    public Poliza cancelar(Long id) {
        Poliza poliza = obtener(id);
        poliza.cancelar(); // cascada: cancela tambien todos sus riesgos
        coreIntegrationService.notificarEvento("CANCELACION", poliza.getId());
        return poliza;
    }

    @Transactional
    public Riesgo agregarRiesgo(Long polizaId, RiesgoRequest request) {
        Poliza poliza = obtener(polizaId);
        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new BusinessException("Solo las polizas de tipo COLECTIVA permiten agregar riesgos");
        }
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No se pueden agregar riesgos a una poliza cancelada");
        }
        Tercero asegurado = terceroService.obtenerOCrear(request.asegurado());
        Tercero beneficiario = terceroService.obtenerOCrear(request.beneficiario());
        Riesgo riesgo = new Riesgo(request.descripcion(), asegurado, beneficiario);
        poliza.agregarRiesgo(riesgo);
        polizaRepository.save(poliza);
        coreIntegrationService.notificarEvento("ACTUALIZACION", poliza.getId());
        // El riesgo recien creado es el ultimo de la lista tras agregarlo
        return poliza.getRiesgos().get(poliza.getRiesgos().size() - 1);
    }
}
