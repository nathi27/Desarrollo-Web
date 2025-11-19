/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pasteleria.repository;

import com.pasteleria.domain.PedidoPersonalizado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoPersonalizadoRepository extends JpaRepository<PedidoPersonalizado, Long> {
    List<PedidoPersonalizado> findByUsuarioIdUsuario(Long idUsuario);
    List<PedidoPersonalizado> findByEstado(String estado);
}
