package com.segurosbolivar.polizaapi.config;

import com.segurosbolivar.polizaapi.model.*;
import com.segurosbolivar.polizaapi.repository.EventoNotificacionRepository;
import com.segurosbolivar.polizaapi.repository.PolizaRepository;
import com.segurosbolivar.polizaapi.repository.RenovacionPolizaRepository;
import com.segurosbolivar.polizaapi.repository.TerceroRepository;
import com.segurosbolivar.polizaapi.repository.TipoDocumentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Carga datos de ejemplo (una poliza Individual y una Colectiva, con sus terceros y
 * riesgos) si la base esta vacia, para que la API tenga algo que mostrar apenas arranca
 * sin depender de que alguien la use primero — util para probar Swagger o el front sin
 * tener que crear todo a mano.
 *
 * Corre despues de TipoDocumentoSeeder (@Order(2) vs @Order(1)) porque Tercero necesita
 * que el catalogo de TipoDocumento ya exista. Es idempotente: si ya hay polizas (datos
 * reales o de un arranque anterior), no hace nada.
 */
@Component
@Order(2)
public class DemoDataSeeder implements CommandLineRunner {

    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TerceroRepository terceroRepository;
    private final PolizaRepository polizaRepository;
    private final RenovacionPolizaRepository renovacionPolizaRepository;
    private final EventoNotificacionRepository eventoNotificacionRepository;

    public DemoDataSeeder(TipoDocumentoRepository tipoDocumentoRepository,
                           TerceroRepository terceroRepository,
                           PolizaRepository polizaRepository,
                           RenovacionPolizaRepository renovacionPolizaRepository,
                           EventoNotificacionRepository eventoNotificacionRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.terceroRepository = terceroRepository;
        this.polizaRepository = polizaRepository;
        this.renovacionPolizaRepository = renovacionPolizaRepository;
        this.eventoNotificacionRepository = eventoNotificacionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (polizaRepository.count() > 0) {
            return;
        }

        TipoDocumento cc = tipoDocumentoRepository.findByCodigo("CC")
                .orElseThrow(() -> new IllegalStateException("Catalogo TipoDocumento no sembrado aun"));
        TipoDocumento nit = tipoDocumentoRepository.findByCodigo("NIT")
                .orElseThrow(() -> new IllegalStateException("Catalogo TipoDocumento no sembrado aun"));

        Tercero juanPerez = terceroRepository.save(new Tercero(cc, "1000111222", "Juan Perez",
                TipoPersona.NATURAL, "juan.perez@example.com", "3001234567"));
        Tercero inmobiliariaBolivar = terceroRepository.save(new Tercero(nit, "900123456", "Inmobiliaria Bolivar SAS",
                TipoPersona.JURIDICA, "contacto@inmobiliariabolivar.com", "6015551234"));
        Tercero mariaGomez = terceroRepository.save(new Tercero(cc, "1000222333", "Maria Gomez",
                TipoPersona.NATURAL, "maria.gomez@example.com", "3007654321"));
        Tercero carlosRodriguez = terceroRepository.save(new Tercero(cc, "1000333444", "Carlos Rodriguez",
                TipoPersona.NATURAL, "carlos.rodriguez@example.com", "3009876543"));

        // Poliza INDIVIDUAL: tomador = asegurado = arrendatario; beneficiario = arrendador.
        Poliza polizaIndividual = new Poliza(TipoPoliza.INDIVIDUAL, LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31), new BigDecimal("1500000"), juanPerez);
        polizaIndividual.asignarRiesgoIndividual(
                new Riesgo("Apartamento 501, Edificio Central, Bogota", juanPerez, inmobiliariaBolivar));
        polizaIndividual = polizaRepository.save(polizaIndividual);
        eventoNotificacionRepository.save(
                new EventoNotificacion(polizaIndividual, TipoEventoNotificacion.CREACION, CanalNotificacion.EMAIL));

        // Renovacion ya ocurrida sobre la individual, para poder probar el historial sin
        // tener que disparar una renovacion manualmente primero.
        BigDecimal ipc = new BigDecimal("0.065");
        BigDecimal canonAnterior = polizaIndividual.getValorCanon();
        BigDecimal primaAnterior = polizaIndividual.getValorPrima();
        polizaIndividual.renovar(ipc);
        polizaRepository.save(polizaIndividual);
        renovacionPolizaRepository.save(new RenovacionPoliza(
                polizaIndividual, canonAnterior, polizaIndividual.getValorCanon(),
                ipc, primaAnterior, polizaIndividual.getValorPrima()));

        // Poliza COLECTIVA: tomador es la inmobiliaria; cada riesgo trae su propio
        // asegurado/beneficiario (Modulo 1, seccion 3).
        Poliza polizaColectiva = new Poliza(TipoPoliza.COLECTIVA, LocalDate.of(2026, 2, 1),
                LocalDate.of(2027, 1, 31), new BigDecimal("800000"), inmobiliariaBolivar);
        polizaColectiva.agregarRiesgo(
                new Riesgo("Local comercial 12, Centro Comercial Plaza", mariaGomez, inmobiliariaBolivar));
        polizaColectiva.agregarRiesgo(
                new Riesgo("Bodega 3, Zona Industrial Norte", carlosRodriguez, inmobiliariaBolivar));
        polizaColectiva = polizaRepository.save(polizaColectiva);

        EventoNotificacion eventoColectiva = new EventoNotificacion(
                polizaColectiva, TipoEventoNotificacion.CREACION, CanalNotificacion.EMAIL);
        eventoColectiva.marcarEnviado();
        eventoNotificacionRepository.save(eventoColectiva);
    }
}
