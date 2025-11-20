/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pasteleria.controler;

import com.pasteleria.domain.Producto;
import com.pasteleria.service.ProductoService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductoController {

    private final ProductoService servicio;

    public ProductoController(ProductoService s) {
        this.servicio = s;
    }

    @GetMapping("/producto/listado")
    public String listado(Model model) {
        model.addAttribute("productos", servicio.listarActivos());
        return "producto/listado";
    }

    // PRIMERO pon las rutas específicas
    @GetMapping("/producto/ingredientes")
    public String ingredientes(Model model) {
        return "producto/ingredientes";
    }

    @GetMapping("/producto/informacion")
    public String informacion(Model model) {
        return "producto/informacion";
    }

    // LUEGO pon la ruta con parámetro {id}
    @GetMapping("/producto/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Producto producto = servicio.ver(id);
        model.addAttribute("producto", producto);
        model.addAttribute("tamanos", servicio.tamanos(id));
        model.addAttribute("sabores", servicio.sabores(id));
        model.addAttribute("ingredientes", servicio.ingredientes(id));
        model.addAttribute("stockDisponible", producto != null ? producto.getStock() : 0);

        return "producto/detalle";
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        // Obtiene todos los productos activos
        List<Producto> todosProductos = servicio.listarActivos();

        // Filtra por categorías basado en el nombre
        List<Producto> piesTortas = todosProductos.stream()
                .filter(p -> p.getNombre().contains("Pie")
                || p.getNombre().contains("Torta")
                || p.getNombre().contains("Pavlova"))
                .collect(Collectors.toList());

        List<Producto> macarons = todosProductos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains("macaron")
                || p.getNombre().toLowerCase().contains("dona")
                || p.getNombre().toLowerCase().contains("brownie"))
                .collect(Collectors.toList());

        List<Producto> galletas = todosProductos.stream()
                .filter(p -> p.getNombre().contains("Galleta")
                || p.getNombre().contains("Brookie"))
                .collect(Collectors.toList());

        List<Producto> pasteles = todosProductos.stream()
                .filter(p -> p.getNombre().contains("Pastel")
                || p.getNombre().contains("Lunch"))
                .collect(Collectors.toList());

        model.addAttribute("productosPiesTortas", piesTortas);
        model.addAttribute("productosMacarons", macarons);
        model.addAttribute("productosGalletas", galletas);
        model.addAttribute("productosPasteles", pasteles);

        return "producto/catalogo";
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "producto/carrito";
    }
}
