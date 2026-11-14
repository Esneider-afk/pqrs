package com.uts.pqrs.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResumenPanel {
    private int total;
    private int verde;
    private int amarillo;
    private int rojo;
    private int respondidas;
    /** Porcentaje de respondidas dentro del término; null si aún no hay respondidas. */
    private Integer cumplimiento;
    private List<SolicitudFila> urgentes;
}