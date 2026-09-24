package com.segurosbolivar.polizaapi.service;

import com.segurosbolivar.polizaapi.dto.TerceroRequest;
import com.segurosbolivar.polizaapi.exception.BusinessException;
import com.segurosbolivar.polizaapi.model.Tercero;
import com.segurosbolivar.polizaapi.model.TipoDocumento;
import com.segurosbolivar.polizaapi.repository.TerceroRepository;
import com.segurosbolivar.polizaapi.repository.TipoDocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resuelve un Tercero por (tipo_documento, numero_documento): si ya existe (porque ya
 * es tomador/asegurado/beneficiario de otra poliza) lo reutiliza en vez de duplicarlo.
 */
@Service
public class TerceroService {

    private final TerceroRepository terceroRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TerceroService(TerceroRepository terceroRepository, TipoDocumentoRepository tipoDocumentoRepository) {
        this.terceroRepository = terceroRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @Transactional
    public Tercero obtenerOCrear(TerceroRequest request) {
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findByCodigo(request.tipoDocumentoCodigo())
                .orElseThrow(() -> new BusinessException(
                        "Tipo de documento no reconocido: " + request.tipoDocumentoCodigo()));

        return terceroRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, request.numeroDocumento())
                .orElseGet(() -> terceroRepository.save(new Tercero(
                        tipoDocumento,
                        request.numeroDocumento(),
                        request.nombre(),
                        request.tipoPersona(),
                        request.correo(),
                        request.telefono())));
    }
}
