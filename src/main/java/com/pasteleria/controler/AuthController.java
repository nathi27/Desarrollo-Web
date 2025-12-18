/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pasteleria.controler;

import com.pasteleria.dto.RegistroUsuarioForm;
import com.pasteleria.dto.SolicitarCodigoForm;
import com.pasteleria.dto.ConfirmarCodigoForm;
import com.pasteleria.dto.CambiarPasswordForm;
import com.pasteleria.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller

public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    
    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("registroUsuarioForm", new RegistroUsuarioForm());
        return "auth/registro"; 
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @Valid @ModelAttribute("registroUsuarioForm") RegistroUsuarioForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        String correoNormalizado = form.getCorreo() != null
                ? form.getCorreo().toLowerCase()
                : null;

        
        if (correoNormalizado != null && usuarioService.existeCorreo(correoNormalizado)) {
            result.rejectValue("correo", "correo.duplicado",
                    "Ya existe una cuenta registrada con este correo");
        }

        
        if (form.getPassword() != null && form.getConfirmarPassword() != null
                && !form.getPassword().equals(form.getConfirmarPassword())) {
            result.rejectValue("confirmarPassword", "password.noCoincide",
                    "Las contraseñas no coinciden");
        }

        
        if (form.getPassword() != null && !usuarioService.passwordValida(form.getPassword())) {
            result.rejectValue("password", "password.debil",
                    "La contraseña debe tener al menos 8 caracteres y una mayúscula");
        }

        if (result.hasErrors()) {
            return "auth/registro";
        }

        
        form.setCorreo(correoNormalizado);
        usuarioService.registrarNuevoUsuario(form);

        redirectAttributes.addFlashAttribute("mensajeExito",
                "Tu cuenta se ha creado correctamente. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }

    
    @GetMapping("/recuperar")
    public String mostrarFormularioRecuperar(Model model) {
        model.addAttribute("solicitarCodigoForm", new SolicitarCodigoForm());
        return "auth/recuperar"; 
    }

    
    @PostMapping("/recuperar")
    public String procesarRecuperar(
            @Valid @ModelAttribute("solicitarCodigoForm") SolicitarCodigoForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/recuperar";
        }

        String correo = form.getCorreo().toLowerCase();
        usuarioService.generarCodigoRecuperacion(correo);

        
        redirectAttributes.addFlashAttribute("mensajeInfo",
                "Si el correo está registrado, se ha enviado un código de verificación.");

        
        redirectAttributes.addAttribute("correo", correo);

        return "redirect:/recuperar/confirmar";
    }

    
    @GetMapping("/recuperar/confirmar")
    public String mostrarFormularioConfirmarCodigo(@RequestParam(value = "correo", required = false) String correo,
            Model model) {

        ConfirmarCodigoForm form = new ConfirmarCodigoForm();
        if (correo != null) {
            form.setCorreo(correo);
        }
        model.addAttribute("confirmarCodigoForm", form);
        return "auth/confirmar-codigo"; 
    }

    
    @PostMapping("/recuperar/confirmar")
    public String procesarConfirmarCodigo(
            @Valid @ModelAttribute("confirmarCodigoForm") ConfirmarCodigoForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/confirmar-codigo";
        }

        String correo = form.getCorreo().toLowerCase();
        boolean valido = usuarioService.validarCodigo(correo, form.getCodigo());

        if (!valido) {
            result.rejectValue("codigo", "codigo.invalido",
                    "El código es inválido o ha expirado.");
            return "auth/confirmar-codigo";
        }

        
        redirectAttributes.addAttribute("correo", correo);

        return "redirect:/recuperar/cambiar";
    }

    
    @GetMapping("/recuperar/cambiar")
    public String mostrarFormularioCambiarPassword(@RequestParam(value = "correo", required = false) String correo,
            Model model) {

        CambiarPasswordForm form = new CambiarPasswordForm();
        form.setCorreo(correo);
        model.addAttribute("cambiarPasswordForm", form);
        return "auth/cambiar-password"; 
    }

    
    @PostMapping("/recuperar/cambiar")
    public String procesarCambiarPassword(
            @Valid @ModelAttribute("cambiarPasswordForm") CambiarPasswordForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        
        if (form.getNuevaPassword() != null && form.getConfirmarPassword() != null
                && !form.getNuevaPassword().equals(form.getConfirmarPassword())) {
            result.rejectValue("confirmarPassword", "password.noCoincide",
                    "Las contraseñas no coinciden");
        }

        
        if (form.getNuevaPassword() != null && !usuarioService.passwordValida(form.getNuevaPassword())) {
            result.rejectValue("nuevaPassword", "password.debil",
                    "La contraseña debe tener al menos 8 caracteres y una mayúscula");
        }

        if (result.hasErrors()) {
            return "auth/cambiar-password";
        }

        String correo = form.getCorreo().toLowerCase();
        boolean cambioOk = usuarioService.cambiarPassword(correo, form.getNuevaPassword());

        if (!cambioOk) {
            result.reject("error.cambio",
                    "No se pudo actualizar la contraseña. Intente de nuevo.");
            return "auth/cambiar-password";
        }

        redirectAttributes.addFlashAttribute("mensajeExito",
                "Tu contraseña ha sido actualizada correctamente.");
        return "redirect:/login";
    }

}
