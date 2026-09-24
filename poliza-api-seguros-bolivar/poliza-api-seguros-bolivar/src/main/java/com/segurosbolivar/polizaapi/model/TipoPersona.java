package com.segurosbolivar.polizaapi.model;

/**
 * Se modela como enumerado, no como catalogo aparte: es una distincion fija y
 * universal (no hay un tercer valor esperando a aparecer), a diferencia de
 * TipoDocumento (ver esa clase).
 */
public enum TipoPersona {
    NATURAL,
    JURIDICA
}
