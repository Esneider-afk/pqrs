package com.uts.pqrs.service;

import com.uts.pqrs.model.Dependencia;
import com.uts.pqrs.model.Prioridad;
import com.uts.pqrs.model.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClasificadorServiceTest {

    private final ClasificadorService servicio = new ClasificadorService(null);

    private final List<Dependencia> dependencias = List.of(
            new Dependencia("Atención al Ciudadano", "atencion,informacion,orientacion,turno"),
            new Dependencia("Contratación", "contrato,contratacion,licitacion,proveedor"),
            new Dependencia("Infraestructura y Servicios Generales", "infraestructura,mantenimiento,senalizacion,edificio,aseo"));

    @Test
    void solicitudDeCopiaDeContrato() {
        var s = servicio.clasificar("Solicito copia del contrato de prestación de servicios", dependencias);
        assertEquals(TipoSolicitud.SOLICITUD_DOCUMENTOS, s.tipo());
        assertEquals("Contratación", s.dependencia().getNombre());
        assertEquals(Prioridad.MEDIA, s.prioridad());
    }

    @Test
    void casoUrgenteTienePrioridadAlta() {
        var s = servicio.clasificar("Urgente: mi hijo menor de edad no recibe atención", dependencias);
        assertEquals(TipoSolicitud.PETICION, s.tipo());
        assertEquals(Prioridad.ALTA, s.prioridad());
    }

    @Test
    void sugerenciaTienePrioridadBaja() {
        var s = servicio.clasificar("Sugiero mejorar la señalización de la entrada", dependencias);
        assertEquals(TipoSolicitud.SUGERENCIA, s.tipo());
        assertEquals("Infraestructura y Servicios Generales", s.dependencia().getNombre());
        assertEquals(Prioridad.BAJA, s.prioridad());
    }

    @Test
    void denunciaEsPrioridadAltaYUsaDependenciaPorDefecto() {
        var s = servicio.clasificar("Quiero denunciar un acto de corrupción", dependencias);
        assertEquals(TipoSolicitud.DENUNCIA, s.tipo());
        assertEquals(Prioridad.ALTA, s.prioridad());
        assertEquals("Atención al Ciudadano", s.dependencia().getNombre());
    }

    @Test
    void textoVacioUsaValoresPorDefecto() {
        var s = servicio.clasificar("", dependencias);
        assertEquals(TipoSolicitud.PETICION, s.tipo());
        assertEquals(Prioridad.MEDIA, s.prioridad());
        assertEquals("Atención al Ciudadano", s.dependencia().getNombre());
    }
}