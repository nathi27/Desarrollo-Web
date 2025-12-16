package com.pasteleria.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SobreNosotrosController {
    
    @GetMapping("/sobre-nosotros")
    public String mostrarSobreNosotros(Model model) {
        model.addAttribute("pagina", "sobre-nosotros");
        return "sobre-nosotros/sobre-nosotros";
    }
}