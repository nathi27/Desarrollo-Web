/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.pasteleria.repository;

import com.pasteleria.domain.ProductoTamano;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoTamanoRepository extends JpaRepository<ProductoTamano, Long> {
    List<ProductoTamano> findByProductoIdProducto(Long idProducto);
}
