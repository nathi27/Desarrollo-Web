/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.pasteleria.controler;

import com.pasteleria.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductoController {
    private final ProductoService servicio;
    public ProductoController(ProductoService s){ this.servicio=s; }

    @GetMapping("/producto/listado")
    public String listado(Model model){
        model.addAttribute("productos", servicio.listarActivos());
        return "producto/listado";
    }

    @GetMapping("/producto/{id}")
    public String detalle(@PathVariable Long id, Model model){
        model.addAttribute("producto", servicio.ver(id));
        model.addAttribute("tamanos", servicio.tamanos(id));
        model.addAttribute("sabores", servicio.sabores(id));
        model.addAttribute("ingredientes", servicio.ingredientes(id));
        return "producto/detalle";
    }

}
