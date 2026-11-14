package com.uts.pqrs.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DiasHabilesServiceTest {

    private final DiasHabilesService servicio = new DiasHabilesService();

    @Test
    void finDeSemanaNoEsHabil() {
        assertFalse(servicio.esHabil(LocalDate.of(2026, 3, 7)));   // sábado
        assertFalse(servicio.esHabil(LocalDate.of(2026, 3, 8)));   // domingo
        assertTrue(servicio.esHabil(LocalDate.of(2026, 3, 9)));    // lunes
    }

    @Test
    void festivoNoEsHabil() {
        assertFalse(servicio.esHabil(LocalDate.of(2026, 3, 23)));  // San José (trasladado)
    }

    @Test
    void sumarCincoDiasHabilesSimples() {
        assertEquals(LocalDate.of(2026, 3, 9), servicio.sumar(LocalDate.of(2026, 3, 2), 5));
    }

    @Test
    void sumarSaltaFinDeSemanaYFestivo() {
        // Viernes 20-mar: sáb, dom y lunes 23 (festivo) no cuentan -> mar 24 (1), mié 25 (2)
        assertEquals(LocalDate.of(2026, 3, 25), servicio.sumar(LocalDate.of(2026, 3, 20), 2));
    }

    @Test
    void sumarSaltaSemanaSanta() {
        // Mié 1-abr: jue 2 y vie 3 son festivos -> el primer hábil es lunes 6-abr
        assertEquals(LocalDate.of(2026, 4, 6), servicio.sumar(LocalDate.of(2026, 4, 1), 1));
    }

    @Test
    void diasHabilesEntreFechas() {
        assertEquals(5, servicio.diasHabilesEntre(LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 9)));
        assertEquals(0, servicio.diasHabilesEntre(LocalDate.of(2026, 3, 9), LocalDate.of(2026, 3, 9)));
        assertEquals(-5, servicio.diasHabilesEntre(LocalDate.of(2026, 3, 9), LocalDate.of(2026, 3, 2)));
    }
}