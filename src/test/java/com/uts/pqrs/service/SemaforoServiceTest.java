package com.uts.pqrs.service;

import com.uts.pqrs.model.EstadoSolicitud;
import com.uts.pqrs.model.Semaforo;
import com.uts.pqrs.model.Solicitud;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SemaforoServiceTest {

    private final SemaforoService servicio = new SemaforoService(new DiasHabilesService());

    private Solicitud solicitud(EstadoSolicitud estado) {
        Solicitud s = new Solicitud();
        s.setEstado(estado);
        s.setFechaVencimiento(LocalDate.of(2026, 3, 9)); // lunes
        return s;
    }

    @Test
    void verdeConMasDeCincoDiasHabiles() {
        // 27-feb (viernes) -> 6 días hábiles hasta el 9-mar
        assertEquals(Semaforo.VERDE, servicio.calcular(solicitud(EstadoSolicitud.RADICADA), LocalDate.of(2026, 2, 27)));
    }

    @Test
    void amarilloEnElLimiteDeCincoDias() {
        assertEquals(Semaforo.AMARILLO, servicio.calcular(solicitud(EstadoSolicitud.RADICADA), LocalDate.of(2026, 3, 2)));
    }

    @Test
    void amarilloElDiaDelVencimiento() {
        assertEquals(Semaforo.AMARILLO, servicio.calcular(solicitud(EstadoSolicitud.EN_TRAMITE), LocalDate.of(2026, 3, 9)));
    }

    @Test
    void rojoUnDiaDespuesDelVencimiento() {
        assertEquals(Semaforo.ROJO, servicio.calcular(solicitud(EstadoSolicitud.RADICADA), LocalDate.of(2026, 3, 10)));
    }

    @Test
    void grisSiYaFueRespondida() {
        assertEquals(Semaforo.GRIS, servicio.calcular(solicitud(EstadoSolicitud.RESPONDIDA), LocalDate.of(2026, 6, 1)));
    }
}