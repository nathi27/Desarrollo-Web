/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pasteleria.repository;

import com.pasteleria.domain.OpcionPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OpcionPedidoRepository extends JpaRepository<OpcionPedido, Long> {
    List<OpcionPedido> findByCategoriaAndActivoTrue(String categoria);
    List<OpcionPedido> findByCategoria(String categoria);
}