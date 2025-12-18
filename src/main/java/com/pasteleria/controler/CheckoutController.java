package com.pasteleria.controler;

import com.pasteleria.domain.*;
import com.pasteleria.service.CompraDto;
import com.pasteleria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired private PedidoRepository pedidoRepository; 
    @Autowired private DetallePedidoRepository detalleRepository; 
    @Autowired private ProductoRepository productoRepository;

    
    @PostMapping("/procesar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> procesarCompra(@RequestBody CompraDto compra) {
        Map<String, Object> response = new HashMap<>();

        try {
            
            Pedido pedido = new Pedido();
            pedido.setFecha(LocalDateTime.now());
            pedido.setMetodoPago(compra.getMetodoPago());
            pedido.setTotal(compra.getTotal());
            pedido.setEstado("Completado");
            
            pedido.setIdUsuario(1L); 

            Pedido pedidoGuardado = pedidoRepository.save(pedido);

            
            for (CompraDto.ItemCarritoDto item : compra.getItems()) {
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedidoGuardado);
                detalle.setProducto(productoRepository.findById(item.getId()).orElse(null));
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getPrecio());
                detalleRepository.save(detalle);
            }

            response.put("success", true);
            response.put("idPedido", pedidoGuardado.getIdPedido());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    
    @GetMapping("/confirmacion/{id}")
    public String confirmacion(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        model.addAttribute("pedido", pedido);
        return "pedidos/confirmacion";
    }
}
