package com.uts.pqrs.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EstadoSolicitud {
    RADICADA("Radicada"),
    EN_TRAMITE("En trámite"),
    RESPONDIDA("Respondida");

    private final String etiqueta;
}