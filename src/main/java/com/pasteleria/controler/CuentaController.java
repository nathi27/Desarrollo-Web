/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pasteleria.controler;

import com.pasteleria.domain.Usuario;
import com.pasteleria.dto.ActualizarCuentaForm;
import com.pasteleria.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CuentaController {
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/informacion-cuenta")
    public String verInformacionCuenta(Authentication auth, Model model, RedirectAttributes ra) {
        if (auth == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión.");
            return "redirect:/login";
        }

        String correo = auth.getName();
        Optional<Usuario> opt = usuarioService.obtenerPorCorreo(correo);

        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "No se pudo cargar tu información de cuenta.");
            return "redirect:/";
        }

        Usuario u = opt.get();

        ActualizarCuentaForm form = new ActualizarCuentaForm();
        form.setNombre(u.getNombre());
        form.setCorreo(u.getCorreo());

        model.addAttribute("form", form);
        return "auth/informacion-cuenta";
    }

    @PostMapping("/informacion-cuenta")
    public String actualizarCuenta(
            @Valid @ModelAttribute("form") ActualizarCuentaForm form,
            BindingResult br,
            Authentication auth,
            Model model,
            RedirectAttributes ra,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (auth == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión.");
            return "redirect:/login";
        }

        Optional<Usuario> opt = usuarioService.obtenerPorCorreo(auth.getName());
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "No se pudo cargar tu cuenta.");
            return "redirect:/";
        }

        Usuario u = opt.get();

        
        if (!br.hasFieldErrors("correo")) {
            if (usuarioService.correoEnUsoPorOtroUsuario(form.getCorreo(), u.getIdUsuario())) {
                br.rejectValue("correo", "correo.duplicado", "Ese correo ya está en uso.");
            }
        }

        
        boolean deseaCambiarPassword = tieneTexto(form.getPasswordActual())
                || tieneTexto(form.getNuevaPassword())
                || tieneTexto(form.getConfirmarPassword());

        if (deseaCambiarPassword) {
            if (!tieneTexto(form.getPasswordActual())) {
                br.rejectValue("passwordActual", "passwordActual.requerida", "Debes escribir tu contraseña actual.");
            }
            if (!tieneTexto(form.getNuevaPassword())) {
                br.rejectValue("nuevaPassword", "nuevaPassword.requerida", "Debes escribir la nueva contraseña.");
            }
            if (!tieneTexto(form.getConfirmarPassword())) {
                br.rejectValue("confirmarPassword", "confirmarPassword.requerida", "Debes confirmar la nueva contraseña.");
            }

            if (!br.hasFieldErrors("passwordActual")) {
                if (!usuarioService.passwordActualCorrecta(u, form.getPasswordActual())) {
                    br.rejectValue("passwordActual", "passwordActual.incorrecta", "La contraseña actual no coincide.");
                }
            }

            if (tieneTexto(form.getNuevaPassword()) && tieneTexto(form.getConfirmarPassword())) {
                String n1 = form.getNuevaPassword().trim();
                String n2 = form.getConfirmarPassword().trim();
                if (!n1.equals(n2)) {
                    br.rejectValue("confirmarPassword", "confirmarPassword.noCoincide", "La confirmación no coincide.");
                }
            }
        }

        if (br.hasErrors()) {
            return "auth/informacion-cuenta";
        }

        
        usuarioService.aplicarCambiosCuenta(u, form);

        
        new SecurityContextLogoutHandler().logout(request, response, auth);

        ra.addFlashAttribute("msgExito", "Cuenta actualizada. Inicia sesión nuevamente con tus nuevos datos.");
        return "redirect:/login";
    }

    private boolean tieneTexto(String s) {
        return s != null && s.trim().length() > 0;
    }
    
}
