package com.uts.pqrs.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoSolicitud {
    PETICION("Petición", 15),
    QUEJA("Queja", 15),
    RECLAMO("Reclamo", 15),
    SUGERENCIA("Sugerencia", 15),
    DENUNCIA("Denuncia", 15),
    CONSULTA("Consulta", 30),
    SOLICITUD_DOCUMENTOS("Solicitud de documentos", 10);

    private final String etiqueta;
    private final int diasHabiles;
}