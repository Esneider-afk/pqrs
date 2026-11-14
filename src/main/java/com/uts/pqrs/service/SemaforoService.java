package com.uts.pqrs.service;

import com.uts.pqrs.model.EstadoSolicitud;
import com.uts.pqrs.model.Semaforo;
import com.uts.pqrs.model.Solicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SemaforoService {

    /** Con este número de días hábiles (o menos) de margen, la solicitud pasa a amarillo. */
    public static final int UMBRAL_AMARILLO = 5;

    private final DiasHabilesService diasHabiles;

    public Semaforo calcular(Solicitud s) {
        return calcular(s, LocalDate.now());
    }

    public Semaforo calcular(Solicitud s, LocalDate hoy) {
        if (s.getEstado() == EstadoSolicitud.RESPONDIDA) return Semaforo.GRIS;
        if (hoy.isAfter(s.getFechaVencimiento())) return Semaforo.ROJO;
        long restantes = diasHabiles.diasHabilesEntre(hoy, s.getFechaVencimiento());
        return restantes <= UMBRAL_AMARILLO ? Semaforo.AMARILLO : Semaforo.VERDE;
    }

    public long diasRestantes(Solicitud s, LocalDate hoy) {
        return diasHabiles.diasHabilesEntre(hoy, s.getFechaVencimiento());
    }
}