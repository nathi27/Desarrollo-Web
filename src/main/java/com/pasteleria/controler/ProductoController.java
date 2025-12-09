package com.pasteleria.controler;

import com.pasteleria.domain.Producto;
import com.pasteleria.dto.FiltroProductoDTO;
import com.pasteleria.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/producto/ingredientes")
    public String ingredientes(Model model) {
        return "producto/ingredientes";
    }

    @GetMapping("/producto/informacion")
    public String informacion(Model model) {
        return "producto/informacion";
    }

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
    public String catalogo(
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "nombre", required = false) String nombre,
            Model model) {
        
        // Obtiene todos los productos activos
        List<Producto> todosProductos = servicio.listarActivos();
        
        // Crear DTO de filtro
        FiltroProductoDTO filtro = new FiltroProductoDTO(categoria, nombre);
        
        // Filtrar productos según los criterios
        List<Producto> productosFiltrados = todosProductos.stream()
                .filter(p -> filtrarPorCategoria(p, categoria))
                .filter(p -> filtrarPorNombre(p, nombre))
                .collect(Collectors.toList());
        
        // Filtrar por categorías específicas (manteniendo tu lógica original)
        List<Producto> piesTortas = productosFiltrados.stream()
                .filter(p -> p.getNombre().contains("Pie")
                        || p.getNombre().contains("Torta")
                        || p.getNombre().contains("Pavlova"))
                .collect(Collectors.toList());

        List<Producto> macarons = productosFiltrados.stream()
                .filter(p -> p.getNombre().toLowerCase().contains("macaron")
                        || p.getNombre().toLowerCase().contains("dona")
                        || p.getNombre().toLowerCase().contains("brownie"))
                .collect(Collectors.toList());

        List<Producto> galletas = productosFiltrados.stream()
                .filter(p -> p.getNombre().contains("Galleta")
                        || p.getNombre().contains("Brookie"))
                .collect(Collectors.toList());

        List<Producto> pasteles = productosFiltrados.stream()
                .filter(p -> p.getNombre().contains("Pastel")
                        || p.getNombre().contains("Lunch"))
                .collect(Collectors.toList());

        // Agregar atributos al modelo
        model.addAttribute("productosPiesTortas", piesTortas);
        model.addAttribute("productosMacarons", macarons);
        model.addAttribute("productosGalletas", galletas);
        model.addAttribute("productosPasteles", pasteles);
        
        // Agregar el filtro para mantener valores en el formulario
        model.addAttribute("filtro", filtro);
        
        // Agregar lista de categorías fijas para el dropdown
        model.addAttribute("categorias", List.of(
            "Pies & Tortas",
            "Macarons & Donas", 
            "Galletas & Brookies",
            "Pasteles de Celebración"
        ));
        
        // Indicar si hay filtro aplicado
        boolean filtroAplicado = categoria != null && !categoria.isEmpty() 
                || nombre != null && !nombre.isEmpty();
        model.addAttribute("filtroAplicado", filtroAplicado);

        return "producto/catalogo";
    }

    // Métodos auxiliares para filtrar
    private boolean filtrarPorCategoria(Producto producto, String categoria) {
        if (categoria == null || categoria.isEmpty()) {
            return true;
        }
        
        switch (categoria) {
            case "Pies & Tortas":
                return producto.getNombre().contains("Pie")
                    || producto.getNombre().contains("Torta")
                    || producto.getNombre().contains("Pavlova");
            case "Macarons & Donas":
                return producto.getNombre().toLowerCase().contains("macaron")
                    || producto.getNombre().toLowerCase().contains("dona")
                    || producto.getNombre().toLowerCase().contains("brownie");
            case "Galletas & Brookies":
                return producto.getNombre().contains("Galleta")
                    || producto.getNombre().contains("Brookie");
            case "Pasteles de Celebración":
                return producto.getNombre().contains("Pastel")
                    || producto.getNombre().contains("Lunch");
            default:
                return true;
        }
    }
    
    private boolean filtrarPorNombre(Producto producto, String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return true;
        }
        return producto.getNombre().toLowerCase().contains(nombre.toLowerCase());
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "producto/carrito";
    }
    
    
}