/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pasteleria.service;

import com.pasteleria.domain.OpcionPedido;
import com.pasteleria.domain.PedidoPersonalizado;
import com.pasteleria.repository.OpcionPedidoRepository;
import com.pasteleria.repository.PedidoPersonalizadoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PedidoPersonalizadoService {
    
    private final PedidoPersonalizadoRepository pedidoRepo;
    private final OpcionPedidoRepository opcionRepo;

    public PedidoPersonalizadoService(PedidoPersonalizadoRepository p, OpcionPedidoRepository o) {
        this.pedidoRepo = p;
        this.opcionRepo = o;
    }

    public List<OpcionPedido> obtenerOpcionesPorCategoria(String categoria) {
        return opcionRepo.findByCategoriaAndActivoTrue(categoria);
    }

    public PedidoPersonalizado guardarPedido(PedidoPersonalizado pedido) {
        return pedidoRepo.save(pedido);
    }

    public List<PedidoPersonalizado> obtenerPedidosPorUsuario(Long idUsuario) {
        return pedidoRepo.findByUsuarioIdUsuario(idUsuario);
    }
}