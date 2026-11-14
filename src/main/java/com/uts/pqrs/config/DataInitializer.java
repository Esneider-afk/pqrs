package com.uts.pqrs.config;

import com.uts.pqrs.model.*;
import com.uts.pqrs.repository.*;
import com.uts.pqrs.service.DiasHabilesService;
import com.uts.pqrs.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DependenciaRepository dependenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudRepository solicitudRepository;
    private final SeguimientoRepository seguimientoRepository;
    private final SolicitudService solicitudService;
    private final DiasHabilesService diasHabiles;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo-data:true}")
    private boolean demoData;

    @Override
    public void run(String... args) {
        if (dependenciaRepository.count() == 0) {
            dependenciaRepository.saveAll(List.of(
                    new Dependencia("Atención al Ciudadano", "atencion,informacion,orientacion,turno"),
                    new Dependencia("Talento Humano", "nomina,vacaciones,incapacidad,hoja de vida,prestaciones,licencia,cesantias,empleo"),
                    new Dependencia("Contratación", "contrato,contratacion,licitacion,proveedor,convocatoria,oferta,supervisor"),
                    new Dependencia("Tesorería y Financiera", "pago,cobro,factura,reintegro,devolucion,cuenta de cobro,tesoreria,impuesto"),
                    new Dependencia("Infraestructura y Servicios Generales", "infraestructura,mantenimiento,senalizacion,edificio,ascensor,parqueadero,aseo,instalaciones,obra"),
                    new Dependencia("Control Interno Disciplinario", "disciplinario,corrupcion,soborno,fraude,irregularidad,acoso,abuso de autoridad,conducta")
            ));
        }

        if (usuarioRepository.count() == 0) {
            crearUsuario("Administrador", "admin@pqrs.local", "Admin123*", Rol.ADMIN, null);
            crearUsuario("Funcionario Atención", "atencion@pqrs.local", "Func123*", Rol.FUNCIONARIO, "Atención al Ciudadano");
            crearUsuario("Funcionario Contratación", "contratacion@pqrs.local", "Func123*", Rol.FUNCIONARIO, "Contratación");
        }

        if (demoData && solicitudRepository.count() == 0) {
            cargarDemo();
        }
    }

    private void crearUsuario(String nombre, String email, String clave, Rol rol, String dependencia) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(clave));
        u.setRol(rol);
        u.setActivo(true);
        if (dependencia != null) u.setDependencia(dependenciaRepository.findByNombre(dependencia).orElseThrow());
        usuarioRepository.save(u);
    }

    private void cargarDemo() {
        String atencion = "Atención al Ciudadano";
        String contratacion = "Contratación";
        // (tipo, asunto, descripción, nombre, anónimo, días atrás, dependencia, estado, días hasta responder)
        demo(TipoSolicitud.PETICION, "Solicitud de información sobre horarios de atención",
                "Requiero conocer los horarios de atención al público y los canales disponibles para radicar documentos.",
                "María Gómez", false, 2, atencion, EstadoSolicitud.RADICADA, null);
        demo(TipoSolicitud.RECLAMO, "Cobro duplicado en cuenta de cobro",
                "Se me cobró dos veces el mismo valor en la cuenta de cobro del mes anterior y solicito el reintegro.",
                "Carlos Pérez", false, 20, contratacion, EstadoSolicitud.EN_TRAMITE, null);
        demo(TipoSolicitud.QUEJA, "Mala atención en ventanilla",
                "El funcionario de ventanilla me atendió de forma grosera y no resolvió mi solicitud de manera oportuna.",
                "Luz Marina Ortiz", false, 35, atencion, EstadoSolicitud.RADICADA, null);
        demo(TipoSolicitud.DENUNCIA, "Presunta irregularidad en proceso de contratación",
                "Denuncio posibles irregularidades en la adjudicación de un contrato de prestación de servicios.",
                null, true, 45, contratacion, EstadoSolicitud.EN_TRAMITE, null);
        demo(TipoSolicitud.SOLICITUD_DOCUMENTOS, "Copia del contrato de prestación de servicios",
                "Solicito copia del contrato de prestación de servicios suscrito durante el año anterior.",
                "Andrés Rojas", false, 6, contratacion, EstadoSolicitud.RADICADA, null);
        demo(TipoSolicitud.CONSULTA, "Consulta sobre requisitos para licencias",
                "Quisiera saber qué requisitos debo cumplir y qué documentos debo presentar para tramitar una licencia.",
                "Paola Díaz", false, 10, atencion, EstadoSolicitud.RADICADA, null);
        demo(TipoSolicitud.PETICION, "Actualización de datos de contacto",
                "Solicito la actualización de mis datos de contacto en los registros de la entidad.",
                "Jorge Medina", false, 25, atencion, EstadoSolicitud.RESPONDIDA, 5);
        demo(TipoSolicitud.RECLAMO, "Reclamo por demora en respuesta anterior",
                "Reclamo porque mi solicitud anterior no fue respondida y han pasado varias semanas desde que la radiqué.",
                "Sandra Ruiz", false, 30, contratacion, EstadoSolicitud.RESPONDIDA, 25);
    }

    private void demo(TipoSolicitud tipo, String asunto, String descripcion, String nombre, boolean anonimo,
                      int diasAtras, String dependencia, EstadoSolicitud estado, Integer diasHastaRespuesta) {
        LocalDate rad = LocalDate.now().minusDays(diasAtras);
        Solicitud s = new Solicitud();
        s.setRadicado(solicitudService.generarRadicado(rad));
        s.setCodigoConsulta(String.format("DEMO%02d", solicitudRepository.count() + 1));
        s.setTipo(tipo);
        s.setEstado(estado);
        s.setPrioridad(tipo == TipoSolicitud.DENUNCIA ? Prioridad.ALTA : Prioridad.MEDIA);
        s.setAsunto(asunto);
        s.setDescripcion(descripcion);
        s.setAnonimo(anonimo);
        s.setNombreSolicitante(anonimo ? "Anónimo" : nombre);
        s.setDocumento(anonimo ? null : "1098000000");
        s.setEmail(anonimo ? null : "demo@correo.com");
        s.setFechaRadicacion(rad);
        s.setFechaVencimiento(diasHabiles.sumar(rad, tipo.getDiasHabiles()));
        s.setClasificacionSugerida("Datos de demostración");
        s.setDependencia(dependenciaRepository.findByNombre(dependencia).orElseThrow());
        if (estado == EstadoSolicitud.RESPONDIDA && diasHastaRespuesta != null) {
            s.setFechaRespuesta(rad.plusDays(diasHastaRespuesta));
            s.setRespuesta("Respuesta de demostración: se atendió su solicitud conforme a la normativa vigente.");
        }
        s = solicitudRepository.save(s);

        guardarSeguimiento(s, rad.atTime(9, 0), "Radicada", "Solicitud radicada por el ciudadano");
        if (estado != EstadoSolicitud.RADICADA) {
            guardarSeguimiento(s, rad.plusDays(1).atTime(10, 0), "En trámite", "Solicitud tomada por la dependencia");
        }
        if (estado == EstadoSolicitud.RESPONDIDA) {
            guardarSeguimiento(s, s.getFechaRespuesta().atTime(15, 0), "Respondida", "Respuesta emitida al ciudadano");
        }
    }

    private void guardarSeguimiento(Solicitud s, LocalDateTime fecha, String accion, String detalle) {
        Seguimiento seg = new Seguimiento();
        seg.setSolicitud(s);
        seg.setFecha(fecha);
        seg.setAutor("Sistema");
        seg.setAccion(accion);
        seg.setDetalle(detalle);
        seguimientoRepository.save(seg);
    }
}