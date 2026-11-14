package com.uts.pqrs.controller;

import com.uts.pqrs.model.*;
import com.uts.pqrs.repository.DependenciaRepository;
import com.uts.pqrs.repository.UsuarioRepository;
import com.uts.pqrs.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/panel")
@RequiredArgsConstructor
public class PanelController {

    private final SolicitudService solicitudService;
    private final UsuarioRepository usuarioRepository;
    private final DependenciaRepository dependenciaRepository;

    private Usuario actual(Authentication auth) {
        return usuarioRepository.findByEmail(auth.getName()).orElseThrow();
    }

    @GetMapping({"", "/"})
    public String dashboard(Authentication auth, Model model) {
        Usuario u = actual(auth);
        model.addAttribute("usuario", u);
        model.addAttribute("resumen", solicitudService.resumen(u));
        return "panel/dashboard";
    }

    @GetMapping("/solicitudes")
    public String lista(@RequestParam(required = false) EstadoSolicitud estado,
                        @RequestParam(required = false) TipoSolicitud tipo,
                        @RequestParam(required = false) Semaforo semaforo,
                        @RequestParam(required = false) String q,
                        Authentication auth, Model model) {
        Usuario u = actual(auth);
        model.addAttribute("usuario", u);
        model.addAttribute("filas", solicitudService.listar(u, estado, tipo, semaforo, q));
        model.addAttribute("estados", EstadoSolicitud.values());
        model.addAttribute("tipos", TipoSolicitud.values());
        model.addAttribute("semaforos", Semaforo.values());
        model.addAttribute("estadoSel", estado);
        model.addAttribute("tipoSel", tipo);
        model.addAttribute("semaforoSel", semaforo);
        model.addAttribute("q", q);
        return "panel/lista";
    }

    @GetMapping("/solicitudes/{id}")
    public String detalle(@PathVariable Long id, Authentication auth, Model model) {
        Usuario u = actual(auth);
        Solicitud s = solicitudService.obtenerParaUsuario(id, u);
        model.addAttribute("usuario", u);
        model.addAttribute("esAdmin", u.getRol() == Rol.ADMIN);
        model.addAttribute("solicitud", s);
        model.addAttribute("fila", solicitudService.aFila(s));
        model.addAttribute("seguimientos", solicitudService.seguimientos(s));
        model.addAttribute("dependencias", dependenciaRepository.findAll());
        return "panel/detalle";
    }

    @PostMapping("/solicitudes/{id}/tomar")
    public String tomar(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        try {
            solicitudService.tomarEnTramite(id, actual(auth));
            ra.addFlashAttribute("ok", "La solicitud quedó en trámite y asignada a usted.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/panel/solicitudes/" + id;
    }

    @PostMapping("/solicitudes/{id}/dependencia")
    public String reasignar(@PathVariable Long id, @RequestParam Long dependenciaId,
                            Authentication auth, RedirectAttributes ra) {
        try {
            solicitudService.asignarDependencia(id, dependenciaId, actual(auth));
            ra.addFlashAttribute("ok", "Dependencia reasignada correctamente.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/panel/solicitudes/" + id;
    }

    @PostMapping("/solicitudes/{id}/responder")
    public String responder(@PathVariable Long id, @RequestParam String respuesta,
                            Authentication auth, RedirectAttributes ra) {
        try {
            solicitudService.responder(id, respuesta, actual(auth));
            ra.addFlashAttribute("ok", "Respuesta registrada. La solicitud quedó en estado Respondida.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/panel/solicitudes/" + id;
    }
}