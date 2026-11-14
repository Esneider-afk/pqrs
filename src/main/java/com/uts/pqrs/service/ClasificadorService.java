package com.uts.pqrs.service;

import com.uts.pqrs.model.Dependencia;
import com.uts.pqrs.model.Prioridad;
import com.uts.pqrs.model.TipoSolicitud;
import com.uts.pqrs.repository.DependenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Clasificación asistida basada en reglas: puntúa coincidencias de palabras clave
 * (sin tildes, al inicio de palabra) para sugerir tipo, dependencia y prioridad.
 * Es una sugerencia; el funcionario conserva la decisión final.
 */
@Service
@RequiredArgsConstructor
public class ClasificadorService {

    public static final String DEPENDENCIA_POR_DEFECTO = "Atención al Ciudadano";

    public record Sugerencia(TipoSolicitud tipo, Dependencia dependencia, Prioridad prioridad) {}

    private static final Map<TipoSolicitud, List<String>> PALABRAS_TIPO = new LinkedHashMap<>();
    private static final List<String> PALABRAS_URGENTES = List.of(
            "urgent", "riesgo", "peligro", "vida", "salud", "menor de edad", "ninos", "tutela",
            "embargo", "discapacidad", "adulto mayor", "amenaza", "emergencia");

    static {
        PALABRAS_TIPO.put(TipoSolicitud.DENUNCIA, List.of(
                "denunci", "corrupcion", "soborno", "fraude", "irregularidad", "acoso", "abuso de autoridad"));
        PALABRAS_TIPO.put(TipoSolicitud.QUEJA, List.of(
                "quej", "mal servicio", "maltrato", "grosero", "mala atencion", "inconformidad"));
        PALABRAS_TIPO.put(TipoSolicitud.RECLAMO, List.of(
                "reclam", "cobr", "reintegro", "devoluc", "incumplimiento", "no me han respondido"));
        PALABRAS_TIPO.put(TipoSolicitud.SOLICITUD_DOCUMENTOS, List.of(
                "copia", "certificado", "certificacion", "constancia"));
        PALABRAS_TIPO.put(TipoSolicitud.CONSULTA, List.of(
                "consult", "concepto juridico", "quisiera saber", "como puedo"));
        PALABRAS_TIPO.put(TipoSolicitud.SUGERENCIA, List.of(
                "suger", "sugier", "propong", "propuesta", "recomiend", "mejorar", "seria bueno"));
    }

    private final DependenciaRepository dependenciaRepository;

    public Sugerencia clasificar(String texto) {
        return clasificar(texto, dependenciaRepository.findAll());
    }

    public Sugerencia clasificar(String texto, List<Dependencia> dependencias) {
        String t = normalizar(texto);
        TipoSolicitud tipo = detectarTipo(t);
        return new Sugerencia(tipo, detectarDependencia(t, dependencias), detectarPrioridad(t, tipo));
    }

    private TipoSolicitud detectarTipo(String t) {
        TipoSolicitud mejor = TipoSolicitud.PETICION;
        int mejorPuntaje = 0;
        for (Map.Entry<TipoSolicitud, List<String>> e : PALABRAS_TIPO.entrySet()) {
            int puntaje = puntuar(t, e.getValue());
            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejor = e.getKey();
            }
        }
        return mejor;
    }

    private Dependencia detectarDependencia(String t, List<Dependencia> dependencias) {
        Dependencia mejor = null;
        int mejorPuntaje = 0;
        for (Dependencia d : dependencias) {
            if (d.getPalabrasClave() == null) continue;
            List<String> claves = Arrays.stream(d.getPalabrasClave().split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).toList();
            int puntaje = puntuar(t, claves);
            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejor = d;
            }
        }
        if (mejor != null) return mejor;
        return dependencias.stream()
                .filter(d -> DEPENDENCIA_POR_DEFECTO.equals(d.getNombre()))
                .findFirst()
                .orElse(dependencias.isEmpty() ? null : dependencias.get(0));
    }

    private Prioridad detectarPrioridad(String t, TipoSolicitud tipo) {
        if (tipo == TipoSolicitud.DENUNCIA || puntuar(t, PALABRAS_URGENTES) > 0) return Prioridad.ALTA;
        if (tipo == TipoSolicitud.SUGERENCIA) return Prioridad.BAJA;
        return Prioridad.MEDIA;
    }

    private int puntuar(String texto, List<String> claves) {
        int n = 0;
        for (String clave : claves) {
            if (Pattern.compile("\\b" + Pattern.quote(normalizar(clave))).matcher(texto).find()) n++;
        }
        return n;
    }

    public static String normalizar(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}