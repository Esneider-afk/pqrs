package com.uts.pqrs.dto;

import com.uts.pqrs.model.Semaforo;
import com.uts.pqrs.model.Solicitud;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Una solicitud junto con su semáforo calculado al momento de consultarla. */
@Getter
@AllArgsConstructor
public class SolicitudFila {

    private final Solicitud solicitud;
    private final Semaforo semaforo;
    private final long diasRestantes;

    public String getTextoPlazo() {
        if (semaforo == Semaforo.GRIS) return "—";
        if (semaforo == Semaforo.ROJO) {
            return diasRestantes < 0 ? "Vencida hace " + (-diasRestantes) + " d. háb." : "Vencida";
        }
        if (diasRestantes == 0) return "Vence hoy";
        return diasRestantes + " d. háb.";
    }
}