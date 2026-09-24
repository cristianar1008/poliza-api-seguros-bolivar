package com.segurosbolivar.polizaapi.exception;

/** Excepcion para violaciones de reglas de negocio (HTTP 409). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
