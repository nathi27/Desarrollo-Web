package com.pasteleria.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PreguntasFrecuentesController {
    
    @GetMapping("/preguntas-frecuentes")
    public String mostrarPreguntasFrecuentes(Model model) {
        model.addAttribute("pagina", "preguntas-frecuentes");
        return "preguntas-frecuentes/preguntas-frecuentes";
    }
}