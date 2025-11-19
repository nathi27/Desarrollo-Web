package com.pasteleria.controler;

import com.pasteleria.service.PedidoPersonalizadoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pedidos")
public class PedidoPersonalizadoController {

    private final PedidoPersonalizadoService pedidoService;

    public PedidoPersonalizadoController(PedidoPersonalizadoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/personalizado")
    public String pedidoPersonalizado(Model model) {
        model.addAttribute("opcionesProducto", pedidoService.obtenerOpcionesPorCategoria("producto"));
        model.addAttribute("opcionesBizcocho", pedidoService.obtenerOpcionesPorCategoria("sabor_bizcocho"));
        model.addAttribute("opcionesRelleno", pedidoService.obtenerOpcionesPorCategoria("sabor_relleno"));
        model.addAttribute("opcionesTamano", pedidoService.obtenerOpcionesPorCategoria("tamano"));
        
        return "pedidos/personalizado";
    }

    @PostMapping("/procesar")
    public String procesarPedido() {
        // Aquí va la lógica para procesar el pedido
        // Por ahora solo redirige a confirmación
        return "redirect:/pedidos/confirmacion";
    }

    @GetMapping("/confirmacion")
    public String confirmacionPedido() {
        return "pedidos/confirmacion";
    }
}