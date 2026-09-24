package com.segurosbolivar.polizaapi.config;

import com.segurosbolivar.polizaapi.model.TipoDocumento;
import com.segurosbolivar.polizaapi.repository.TipoDocumentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Siembra el catalogo de referencia (Modulo 1, seccion 3) si esta vacio. Se hizo como
 * CommandLineRunner (en vez de data.sql) porque la base es Postgres persistente entre
 * reinicios: con data.sql habria que resolver el orden de inicializacion (Hibernate
 * crea/actualiza el esquema primero) y evitar duplicados en cada arranque de la app;
 * aqui basta con revisar si el catalogo ya tiene datos.
 */
@Component
public class TipoDocumentoSeeder implements CommandLineRunner {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoSeeder(TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @Override
    public void run(String... args) {
        if (tipoDocumentoRepository.count() > 0) {
            return;
        }
        tipoDocumentoRepository.save(new TipoDocumento("CC", "Cedula de ciudadania"));
        tipoDocumentoRepository.save(new TipoDocumento("CE", "Cedula de extranjeria"));
        tipoDocumentoRepository.save(new TipoDocumento("NIT", "Numero de identificacion tributaria"));
        tipoDocumentoRepository.save(new TipoDocumento("PAS", "Pasaporte"));
    }
}
