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
    
   
    @GetMapping("")
    public String mostrarResenas(Model model) {
        // Obtener todos los productos activos para el dropdown
        List<Producto> productos = productoService.listarActivos();
        model.addAttribute("productos", productos);
        return "reseñas/listado";
    }
    
    
    @GetMapping("/crear")
    public String mostrarFormularioResena(
            @RequestParam("idProducto") Long idProducto,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        
        if (idProducto == null) {
            redirectAttributes.addFlashAttribute("error", "Debes seleccionar un producto");
            return "redirect:/reseñas";
        }
        
        
        Producto producto = productoService.ver(idProducto);
        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/reseñas";
        }
        
       
        CrearResenaForm form = new CrearResenaForm();
        form.setIdProducto(idProducto);
        
        model.addAttribute("crearResenaForm", form);
        model.addAttribute("producto", producto);
        
        return "reseñas/crear";
    }
    
    
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

    
    @GetMapping("/mis-reseñas")
    public String misResenas(Model model) {
        
        Long idUsuario = 1L;

        
        List<Resena> misResenas = resenaService.obtenerResenasPorUsuario(idUsuario);
        model.addAttribute("misResenas", misResenas);

        
        List<Resena> resenasComunidad = resenaService.obtenerResenasRecientesExcluyendoUsuario(idUsuario);

        
        if (resenasComunidad.isEmpty()) {
            resenasComunidad = resenaService.obtenerResenasRecientesComunidad();
        }

        
        List<Producto> productos = productoService.listarActivos();

        model.addAttribute("resenasComunidad", resenasComunidad);
        model.addAttribute("productos", productos);

        return "reseñas/mis-reseñas";
    }
    
   
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