package com.segurosbolivar.polizaapi.service;

import com.segurosbolivar.polizaapi.model.Riesgo;
import com.segurosbolivar.polizaapi.repository.RiesgoRepository;
import com.segurosbolivar.polizaapi.port.CoreNotificadorPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final CoreNotificadorPort coreIntegrationService;

    public RiesgoService(RiesgoRepository riesgoRepository, CoreNotificadorPort coreIntegrationService) {
        this.riesgoRepository = riesgoRepository;
        this.coreIntegrationService = coreIntegrationService;
    }

    @Transactional
    public Riesgo cancelar(Long id) {
        Riesgo riesgo = riesgoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Riesgo " + id + " no encontrado"));
        riesgo.cancelar();
        coreIntegrationService.notificarEvento("CANCELACION_RIESGO", riesgo.getPoliza().getId());
        return riesgo;
    }
}
