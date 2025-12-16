package com.pasteleria.controler;

import com.pasteleria.domain.Resena;
import com.pasteleria.domain.Producto;
import com.pasteleria.dto.CrearResenaForm;
import com.pasteleria.service.ResenaService;
import com.pasteleria.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/reseñas")
public class ResenaController {
    
    private final ResenaService resenaService;
    private final ProductoService productoService;
    
    public ResenaController(ResenaService resenaService, ProductoService productoService) {
        this.resenaService = resenaService;
        this.productoService = productoService;
    }
    
    // ======================================================
    // Página principal de reseñas - MODIFICADA
    // ======================================================
    @GetMapping("")
    public String mostrarResenas(Model model) {
        // Obtener todos los productos activos para el dropdown
        List<Producto> productos = productoService.listarActivos();
        model.addAttribute("productos", productos);
        return "reseñas/listado";
    }
    
    // ======================================================
    // Formulario para crear reseña - MODIFICADO
    // Ahora recibe el producto como parámetro GET (idProducto)
    // ======================================================
    @GetMapping("/crear")
    public String mostrarFormularioResena(
            @RequestParam("idProducto") Long idProducto,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Validar que se proporcionó un ID de producto
        if (idProducto == null) {
            redirectAttributes.addFlashAttribute("error", "Debes seleccionar un producto");
            return "redirect:/reseñas";
        }
        
        // Validar que el producto existe
        Producto producto = productoService.ver(idProducto);
        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/reseñas";
        }
        
        // TODO: Validar que el usuario ha comprado este producto
        // Por ahora permitimos cualquier usuario
        
        // Crear formulario con el producto seleccionado
        CrearResenaForm form = new CrearResenaForm();
        form.setIdProducto(idProducto);
        
        model.addAttribute("crearResenaForm", form);
        model.addAttribute("producto", producto);
        
        return "reseñas/crear";
    }
    
    // ======================================================
    // Procesar nueva reseña - MANTENIDO
    // ======================================================
    @PostMapping("/crear")
    public String procesarResena(
            @Valid @ModelAttribute("crearResenaForm") CrearResenaForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            Producto producto = productoService.ver(form.getIdProducto());
            model.addAttribute("producto", producto);
            return "reseñas/crear";
        }
        
        try {
            // TODO: Obtener el ID del usuario logueado de la sesión
            // Por ahora usamos un usuario de prueba (ID 1)
            Long idUsuario = 1L;
            
            resenaService.crearResena(form, idUsuario);
            
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "¡Gracias por tu reseña! Ha sido publicada exitosamente.");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Redirigir al formulario con el producto seleccionado
            return "redirect:/reseñas/crear?idProducto=" + form.getIdProducto();
        }
        
        return "redirect:/producto/" + form.getIdProducto();
    }
    
    // ======================================================
    // Ver reseñas de un producto específico - MANTENIDO
    // ======================================================
    @GetMapping("/producto/{idProducto}")
    public String verResenasProducto(@PathVariable Long idProducto, Model model) {
        Producto producto = productoService.ver(idProducto);
        if (producto == null) {
            return "redirect:/reseñas";
        }
        
        List<Resena> resenas = resenaService.obtenerResenasPorProducto(idProducto);
        Double promedio = resenaService.obtenerPromedioCalificacion(idProducto);
        Integer totalResenas = resenaService.contarResenasAprobadas(idProducto);
        
        model.addAttribute("producto", producto);
        model.addAttribute("resenas", resenas);
        model.addAttribute("promedioCalificacion", promedio);
        model.addAttribute("totalResenas", totalResenas);
        
        return "reseñas/producto";
    }

    // Mis reseñas (del usuario logueado)
    @GetMapping("/mis-reseñas")
    public String misResenas(Model model) {
        // TODO: Obtener el ID del usuario logueado de la sesión
        Long idUsuario = 1L;

        // 1. Obtener las reseñas del usuario logueado
        List<Resena> misResenas = resenaService.obtenerResenasPorUsuario(idUsuario);
        model.addAttribute("misResenas", misResenas);

        // 2. Obtener reseñas recientes de la comunidad (excluyendo al usuario actual)
        List<Resena> resenasComunidad = resenaService.obtenerResenasRecientesExcluyendoUsuario(idUsuario);

        // Si no hay suficientes reseñas excluyendo al usuario, mostrar las más recientes generales
        if (resenasComunidad.isEmpty()) {
            resenasComunidad = resenaService.obtenerResenasRecientesComunidad();
        }

        // 3. Obtener lista de productos para el filtro
        List<Producto> productos = productoService.listarActivos();

        model.addAttribute("resenasComunidad", resenasComunidad);
        model.addAttribute("productos", productos);

        return "reseñas/mis-reseñas";
    }
    
    // ======================================================
    // MÉTODO OBSOLETO - Mantenido por compatibilidad
    // ======================================================
    @GetMapping("/crear/{idProducto}")
    public String mostrarFormularioResenaAntiguo(
            @PathVariable Long idProducto,
            @RequestParam(required = false) Long idPedido,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        redirectAttributes.addAttribute("idProducto", idProducto);
        if (idPedido != null) {
            redirectAttributes.addAttribute("idPedido", idPedido);
        }
        
        return "redirect:/reseñas/crear";
    }
}