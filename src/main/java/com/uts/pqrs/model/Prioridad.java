package com.uts.pqrs.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Prioridad {
    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta");

    private final String etiqueta;
}