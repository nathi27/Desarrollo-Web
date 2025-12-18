package com.pasteleria.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactoController {
    
    @GetMapping("/contacto")
    public String mostrarPaginaContacto(Model model) {
        
        model.addAttribute("pagina", "contacto");
        model.addAttribute("telefono", "8985-2145");
        model.addAttribute("email", "mimisbakeryc@gmail.com");
        model.addAttribute("ubicacion", "El Carmen, Cartago");
        model.addAttribute("instagramUrl", "https://www.instagram.com/mimisbakery__?igsh=MTUxMGk4N2RhbWcyOA==");
        model.addAttribute("instagramUser", "@mimisbakery__");
        model.addAttribute("facebookUrl", "#");
        model.addAttribute("whatsapp", "89852145");
        
        return "contacto/contacto";
    }
}