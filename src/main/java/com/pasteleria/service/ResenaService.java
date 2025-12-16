package com.pasteleria.service;

import com.pasteleria.domain.Resena;
import com.pasteleria.domain.Producto;
import com.pasteleria.domain.Usuario;
import com.pasteleria.domain.Pedido;
import com.pasteleria.dto.CrearResenaForm;
import com.pasteleria.repository.ResenaRepository;
import com.pasteleria.repository.ProductoRepository;
import com.pasteleria.repository.UsuarioRepository;
import com.pasteleria.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ResenaService {
    
    private final ResenaRepository resenaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    
    // Patrones para validación de contenido ofensivo/links
    private static final Pattern LINK_PATTERN = Pattern.compile("https?://[^\\s]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern PALABRAS_OFENSIVAS = Pattern.compile(
        "(?i)\\b(pendejo|estupido|idiota|imbecil|mierda|carajo|verga|puta|prostituta)\\b"
    );
    
    public ResenaService(ResenaRepository resenaRepository, ProductoRepository productoRepository,
                        UsuarioRepository usuarioRepository, PedidoRepository pedidoRepository) {
        this.resenaRepository = resenaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
    }
    
    @Transactional
    public Resena crearResena(CrearResenaForm form, Long idUsuario) {
        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Validar que el producto existe
        Producto producto = productoRepository.findById(form.getIdProducto())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Validar que el usuario no haya reseñado este producto antes
        if (resenaRepository.existsByUsuarioIdUsuarioAndProductoIdProducto(idUsuario, form.getIdProducto())) {
            throw new RuntimeException("Ya has reseñado este producto");
        }
        
        // Validar contenido (lenguaje ofensivo y links)
        validarContenidoResena(form.getComentario());
        
        // Buscar el último pedido del usuario para este producto (opcional)
        Pedido ultimoPedido = null; // Implementar lógica si es necesario
        
        // Crear la reseña
        Resena resena = new Resena();
        resena.setProducto(producto);
        resena.setUsuario(usuario);
        resena.setPedido(ultimoPedido);
        resena.setComentario(form.getComentario());
        resena.setCalificacion(form.getCalificacion());
        resena.setAprobada(true); // Por defecto aprobada, podría cambiarse a moderación
        resena.setFechaCreacion(LocalDateTime.now());
        resena.setFechaModificacion(LocalDateTime.now());
        
        return resenaRepository.save(resena);
    }
    
    private void validarContenidoResena(String comentario) {
        // Validar links no permitidos
        if (LINK_PATTERN.matcher(comentario).find()) {
            throw new RuntimeException("No se permiten enlaces en las reseñas");
        }
        
        // Validar lenguaje ofensivo
        if (PALABRAS_OFENSIVAS.matcher(comentario).find()) {
            throw new RuntimeException("El comentario contiene lenguaje inapropiado");
        }
    }
    
    public List<Resena> obtenerResenasPorProducto(Long idProducto) {
        return resenaRepository.findByProductoIdProductoAndAprobadaTrue(idProducto);
    }
    
    public List<Resena> obtenerResenasPorUsuario(Long idUsuario) {
        return resenaRepository.findByUsuarioIdUsuario(idUsuario);
    }
    
    // NUEVO: Obtener reseñas recientes excluyendo un usuario
    public List<Resena> obtenerResenasRecientesExcluyendoUsuario(Long idUsuarioExcluir) {
        return resenaRepository.findTop10ByUsuarioIdUsuarioNotAndAprobadaTrueOrderByFechaCreacionDesc(idUsuarioExcluir);
    }
    
    // NUEVO: Obtener reseñas recientes de la comunidad
    public List<Resena> obtenerResenasRecientesComunidad() {
        return resenaRepository.findTop10ByAprobadaTrueOrderByFechaCreacionDesc();
    }
    
    public Double obtenerPromedioCalificacion(Long idProducto) {
        Double promedio = resenaRepository.findPromedioCalificacionByProducto(idProducto);
        return promedio != null ? promedio : 0.0;
    }
    
    public Integer contarResenasAprobadas(Long idProducto) {
        return resenaRepository.countResenasAprobadasByProducto(idProducto);
    }
    
    @Transactional
    public void eliminarResena(Long idResena, Long idUsuario) {
        Resena resena = resenaRepository.findById(idResena)
            .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
        
        // Validar que el usuario es el dueño de la reseña
        if (!resena.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("No tienes permiso para eliminar esta reseña");
        }
        
        resenaRepository.delete(resena);
    }
}