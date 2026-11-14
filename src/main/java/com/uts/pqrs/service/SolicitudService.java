package com.uts.pqrs.service;

import com.uts.pqrs.dto.RadicacionForm;
import com.uts.pqrs.dto.ResumenPanel;
import com.uts.pqrs.dto.SolicitudFila;
import com.uts.pqrs.model.*;
import com.uts.pqrs.repository.DependenciaRepository;
import com.uts.pqrs.repository.SeguimientoRepository;
import com.uts.pqrs.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final SolicitudRepository solicitudRepository;
    private final SeguimientoRepository seguimientoRepository;
    private final DependenciaRepository dependenciaRepository;
    private final ClasificadorService clasificador;
    private final DiasHabilesService diasHabiles;
    private final SemaforoService semaforoService;

    // ---------- Radicación y consulta pública ----------

    @Transactional
    public Solicitud radicar(RadicacionForm f) {
        LocalDate hoy = LocalDate.now();
        boolean anonimo = f.isAnonimo() && f.getTipo() == TipoSolicitud.DENUNCIA;
        ClasificadorService.Sugerencia sug = clasificador.clasificar(f.getAsunto() + " " + f.getDescripcion());

        Solicitud s = new Solicitud();
        s.setRadicado(generarRadicado(hoy));
        s.setCodigoConsulta(generarCodigo());
        s.setTipo(f.getTipo());
        s.setEstado(EstadoSolicitud.RADICADA);
        s.setPrioridad(sug.prioridad());
        s.setAsunto(f.getAsunto().trim());
        s.setDescripcion(f.getDescripcion().trim());
        s.setAnonimo(anonimo);
        s.setNombreSolicitante(anonimo ? "Anónimo" : f.getNombre().trim());
        s.setDocumento(anonimo ? null : f.getDocumento().trim());
        s.setEmail(anonimo ? null : vacioANull(f.getEmail()));
        s.setTelefono(anonimo ? null : vacioANull(f.getTelefono()));
        s.setFechaRadicacion(hoy);
        s.setFechaVencimiento(diasHabiles.sumar(hoy, f.getTipo().getDiasHabiles()));
        s.setDependencia(sug.dependencia());
        s.setClasificacionSugerida("Tipo sugerido: " + sug.tipo().getEtiqueta()
                + " | Dependencia: " + (sug.dependencia() != null ? sug.dependencia().getNombre() : "—")
                + " | Prioridad: " + sug.prioridad().getEtiqueta());
        s = solicitudRepository.save(s);

        registrar(s, "Sistema", "Radicada",
                "Solicitud radicada. Dependencia asignada automáticamente: "
                        + (s.getDependencia() != null ? s.getDependencia().getNombre() : "—"));
        return s;
    }

    public String generarRadicado(LocalDate fecha) {
        long n = solicitudRepository.countByFechaRadicacionBetween(
                LocalDate.of(fecha.getYear(), 1, 1), LocalDate.of(fecha.getYear(), 12, 31)) + 1;
        return String.format("PQRS-%d-%06d", fecha.getYear(), n);
    }

    private String generarCodigo() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) sb.append(ALFABETO.charAt(RANDOM.nextInt(ALFABETO.length())));
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public Optional<Solicitud> consultar(String radicado, String codigo) {
        if (radicado == null || codigo == null) return Optional.empty();
        return solicitudRepository.findByRadicadoIgnoreCase(radicado.trim())
                .filter(s -> s.getCodigoConsulta().equalsIgnoreCase(codigo.trim()));
    }

    @Transactional(readOnly = true)
    public List<Seguimiento> seguimientos(Solicitud s) {
        return seguimientoRepository.findBySolicitudOrderByFechaAsc(s);
    }

    public SolicitudFila aFila(Solicitud s) {
        LocalDate hoy = LocalDate.now();
        return new SolicitudFila(s, semaforoService.calcular(s, hoy), semaforoService.diasRestantes(s, hoy));
    }

    // ---------- Panel de funcionarios ----------

    @Transactional(readOnly = true)
    public List<SolicitudFila> listar(Usuario u, EstadoSolicitud estado, TipoSolicitud tipo,
                                      Semaforo semaforo, String q) {
        LocalDate hoy = LocalDate.now();
        List<Solicitud> base;
        if (u.getRol() == Rol.ADMIN) {
            base = solicitudRepository.findAllByOrderByFechaVencimientoAsc();
        } else if (u.getDependencia() != null) {
            base = solicitudRepository.findByDependenciaOrderByFechaVencimientoAsc(u.getDependencia());
        } else {
            base = List.of();
        }
        String filtro = q == null ? "" : ClasificadorService.normalizar(q.trim());

        return base.stream()
                .filter(s -> estado == null || s.getEstado() == estado)
                .filter(s -> tipo == null || s.getTipo() == tipo)
                .filter(s -> filtro.isEmpty() || ClasificadorService.normalizar(
                        s.getRadicado() + " " + s.getAsunto() + " " + s.getNombreSolicitante()).contains(filtro))
                .map(s -> new SolicitudFila(s, semaforoService.calcular(s, hoy), semaforoService.diasRestantes(s, hoy)))
                .filter(f -> semaforo == null || f.getSemaforo() == semaforo)
                .sorted(Comparator.comparing((SolicitudFila f) -> f.getSemaforo() == Semaforo.GRIS)
                        .thenComparing(f -> f.getSolicitud().getFechaVencimiento()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumenPanel resumen(Usuario u) {
        List<SolicitudFila> filas = listar(u, null, null, null, null);
        ResumenPanel r = new ResumenPanel();
        r.setTotal(filas.size());
        r.setVerde(contar(filas, Semaforo.VERDE));
        r.setAmarillo(contar(filas, Semaforo.AMARILLO));
        r.setRojo(contar(filas, Semaforo.ROJO));
        r.setRespondidas(contar(filas, Semaforo.GRIS));

        long aTiempo = filas.stream()
                .filter(f -> f.getSemaforo() == Semaforo.GRIS)
                .filter(f -> !f.getSolicitud().getFechaRespuesta().isAfter(f.getSolicitud().getFechaVencimiento()))
                .count();
        r.setCumplimiento(r.getRespondidas() == 0 ? null
                : (int) Math.round(aTiempo * 100.0 / r.getRespondidas()));
        r.setUrgentes(filas.stream().filter(f -> f.getSemaforo() != Semaforo.GRIS).limit(8).toList());
        return r;
    }

    @Transactional(readOnly = true)
    public Solicitud obtenerParaUsuario(Long id, Usuario u) {
        return cargar(id, u);
    }

    @Transactional
    public void tomarEnTramite(Long id, Usuario u) {
        Solicitud s = cargar(id, u);
        if (s.getEstado() == EstadoSolicitud.RESPONDIDA) {
            throw new IllegalStateException("La solicitud ya fue respondida.");
        }
        s.setEstado(EstadoSolicitud.EN_TRAMITE);
        s.setFuncionarioAsignado(u);
        registrar(s, u.getNombre(), "En trámite", "Solicitud tomada por " + u.getNombre());
    }

    @Transactional
    public void asignarDependencia(Long id, Long dependenciaId, Usuario u) {
        if (u.getRol() != Rol.ADMIN) throw new AccessDeniedException("Solo el administrador puede reasignar.");
        Solicitud s = cargar(id, u);
        if (s.getEstado() == EstadoSolicitud.RESPONDIDA) {
            throw new IllegalStateException("La solicitud ya fue respondida.");
        }
        Dependencia d = dependenciaRepository.findById(dependenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Dependencia no válida."));
        s.setDependencia(d);
        s.setFuncionarioAsignado(null);
        s.setEstado(EstadoSolicitud.RADICADA);
        registrar(s, u.getNombre(), "Reasignada", "Reasignada a la dependencia: " + d.getNombre());
    }

    @Transactional
    public void responder(Long id, String texto, Usuario u) {
        Solicitud s = cargar(id, u);
        if (s.getEstado() == EstadoSolicitud.RESPONDIDA) {
            throw new IllegalStateException("La solicitud ya fue respondida.");
        }
        if (texto == null || texto.trim().length() < 20) {
            throw new IllegalArgumentException("La respuesta debe tener al menos 20 caracteres.");
        }
        s.setRespuesta(texto.trim());
        s.setFechaRespuesta(LocalDate.now());
        s.setEstado(EstadoSolicitud.RESPONDIDA);
        s.setFuncionarioAsignado(u);
        boolean aTiempo = !s.getFechaRespuesta().isAfter(s.getFechaVencimiento());
        registrar(s, u.getNombre(), "Respondida",
                aTiempo ? "Respuesta emitida dentro del término legal." : "Respuesta emitida fuera del término legal.");
    }

    // ---------- Utilidades internas ----------

    private Solicitud cargar(Long id, Usuario u) {
        Solicitud s = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
        if (u.getRol() != Rol.ADMIN) {
            boolean mismaDependencia = u.getDependencia() != null && s.getDependencia() != null
                    && u.getDependencia().getId().equals(s.getDependencia().getId());
            if (!mismaDependencia) throw new AccessDeniedException("No tiene acceso a esta solicitud");
        }
        return s;
    }

    private void registrar(Solicitud s, String autor, String accion, String detalle) {
        Seguimiento seg = new Seguimiento();
        seg.setSolicitud(s);
        seg.setFecha(LocalDateTime.now());
        seg.setAutor(autor);
        seg.setAccion(accion);
        seg.setDetalle(detalle);
        seguimientoRepository.save(seg);
    }

    private int contar(List<SolicitudFila> filas, Semaforo sem) {
        return (int) filas.stream().filter(f -> f.getSemaforo() == sem).count();
    }

    private String vacioANull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}