package com.uts.pqrs.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Service
public class DiasHabilesService {

    /**
     * Festivos de Colombia. Para años posteriores a 2027 hay que agregarlos aquí
     * (o migrar esta lista a una tabla administrable). Verifíquelos contra el calendario oficial.
     */
    private static final Set<LocalDate> FESTIVOS = Set.of(
            // 2026
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 12), LocalDate.of(2026, 3, 23),
            LocalDate.of(2026, 4, 2), LocalDate.of(2026, 4, 3), LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 18), LocalDate.of(2026, 6, 8), LocalDate.of(2026, 6, 15),
            LocalDate.of(2026, 6, 29), LocalDate.of(2026, 7, 20), LocalDate.of(2026, 8, 7),
            LocalDate.of(2026, 8, 17), LocalDate.of(2026, 10, 12), LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 16), LocalDate.of(2026, 12, 8), LocalDate.of(2026, 12, 25),
            // 2027
            LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 11), LocalDate.of(2027, 3, 22),
            LocalDate.of(2027, 3, 25), LocalDate.of(2027, 3, 26), LocalDate.of(2027, 5, 1),
            LocalDate.of(2027, 5, 10), LocalDate.of(2027, 5, 31), LocalDate.of(2027, 6, 7),
            LocalDate.of(2027, 7, 5), LocalDate.of(2027, 7, 20), LocalDate.of(2027, 8, 7),
            LocalDate.of(2027, 8, 16), LocalDate.of(2027, 10, 18), LocalDate.of(2027, 11, 1),
            LocalDate.of(2027, 11, 15), LocalDate.of(2027, 12, 8), LocalDate.of(2027, 12, 25)
    );

    public boolean esHabil(LocalDate fecha) {
        DayOfWeek d = fecha.getDayOfWeek();
        return d != DayOfWeek.SATURDAY && d != DayOfWeek.SUNDAY && !FESTIVOS.contains(fecha);
    }

    /** Suma días hábiles. El día de inicio no cuenta: el día 1 es el siguiente día hábil. */
    public LocalDate sumar(LocalDate inicio, int dias) {
        LocalDate fecha = inicio;
        int sumados = 0;
        while (sumados < dias) {
            fecha = fecha.plusDays(1);
            if (esHabil(fecha)) sumados++;
        }
        return fecha;
    }

    /** Días hábiles en el intervalo (desde, hasta]. Negativo si hasta es anterior a desde. */
    public long diasHabilesEntre(LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde)) return -diasHabilesEntre(hasta, desde);
        long n = 0;
        for (LocalDate f = desde.plusDays(1); !f.isAfter(hasta); f = f.plusDays(1)) {
            if (esHabil(f)) n++;
        }
        return n;
    }
}