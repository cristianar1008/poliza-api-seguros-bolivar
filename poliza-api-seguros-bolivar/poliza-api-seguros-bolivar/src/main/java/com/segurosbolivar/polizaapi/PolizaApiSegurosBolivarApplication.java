package com.segurosbolivar.polizaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // requerido por NotificacionPublisher (consumidor mock del Outbox de notificaciones)
public class PolizaApiSegurosBolivarApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolizaApiSegurosBolivarApplication.class, args);
    }

}
