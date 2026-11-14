package com.uts.pqrs.controller;

import com.uts.pqrs.dto.RadicacionForm;
import com.uts.pqrs.model.Solicitud;
import com.uts.pqrs.model.TipoSolicitud;
import com.uts.pqrs.service.ClasificadorService;
import com.uts.pqrs.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PublicController {

    private final SolicitudService solicitudService;
    private final ClasificadorService clasificador;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tipos", TipoSolicitud.values());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ----- Radicación -----

    @GetMapping("/radicar")
    public String radicarForm(Model model) {
        model.addAttribute("form", new RadicacionForm());
        model.addAttribute("tipos", TipoSolicitud.values());
        return "radicar";
    }

    @PostMapping("/radicar")
    public String radicar(@Valid @ModelAttribute("form") RadicacionForm form, BindingResult result,
                          Model model, RedirectAttributes ra) {
        boolean anonimo = form.isAnonimo() && form.getTipo() == TipoSolicitud.DENUNCIA;
        if (!anonimo) {
            if (vacio(form.getNombre())) {
                result.rejectValue("nombre", "obligatorio", "El nombre es obligatorio");
            }
            if (vacio(form.getDocumento())) {
                result.rejectValue("documento", "obligatorio", "El documento de identidad es obligatorio");
            }
            if (vacio(form.getEmail()) && vacio(form.getTelefono())) {
                result.rejectValue("email", "contacto", "Indique un correo o un teléfono de contacto");
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("tipos", TipoSolicitud.values());
            return "radicar";
        }

        Solicitud s = solicitudService.radicar(form);
        ra.addFlashAttribute("radicado", s.getRadicado());
        ra.addFlashAttribute("codigo", s.getCodigoConsulta());
        ra.addFlashAttribute("vencimiento", s.getFechaVencimiento());
        ra.addFlashAttribute("tipoEtiqueta", s.getTipo().getEtiqueta());
        return "redirect:/radicar/exito";
    }

    @GetMapping("/radicar/exito")
    public String exito(Model model) {
        if (!model.containsAttribute("radicado")) return "redirect:/";
        return "exito";
    }

    // ----- Clasificación asistida (usada por radicar.js) -----

    @PostMapping("/api/clasificar")
    @ResponseBody
    public Map<String, Object> clasificar(@RequestBody Map<String, String> body) {
        String texto = body.getOrDefault("texto", "");
        if (texto.length() > 5000) texto = texto.substring(0, 5000);
        ClasificadorService.Sugerencia s = clasificador.clasificar(texto);
        return Map.of(
                "tipo", s.tipo().name(),
                "tipoEtiqueta", s.tipo().getEtiqueta(),
                "plazo", s.tipo().getDiasHabiles(),
                "dependencia", s.dependencia() != null ? s.dependencia().getNombre() : "—",
                "prioridad", s.prioridad().getEtiqueta());
    }

    // ----- Consulta pública -----

    @GetMapping("/consulta")
    public String consultaForm() {
        return "consulta";
    }

    @PostMapping("/consulta")
    public String consultar(@RequestParam String radicado, @RequestParam String codigo, Model model) {
        model.addAttribute("radicadoIngresado", radicado);
        solicitudService.consultar(radicado, codigo).ifPresentOrElse(s -> {
            model.addAttribute("solicitud", s);
            model.addAttribute("fila", solicitudService.aFila(s));
            model.addAttribute("seguimientos", solicitudService.seguimientos(s));
        }, () -> model.addAttribute("noEncontrado", true));
        return "consulta";
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}