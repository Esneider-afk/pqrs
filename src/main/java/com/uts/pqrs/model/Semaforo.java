package com.uts.pqrs.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Semaforo {
    VERDE("En término", "semaforo-verde"),
    AMARILLO("Por vencer", "semaforo-amarillo"),
    ROJO("Vencida", "semaforo-rojo"),
    GRIS("Respondida", "semaforo-gris");

    private final String etiqueta;
    private final String cssClass;
}