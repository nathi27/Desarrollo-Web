/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.pasteleria.repository;


import com.pasteleria.domain.ProductoSabor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoSaborRepository extends JpaRepository<ProductoSabor, Long> {
    List<ProductoSabor> findByProductoIdProducto(Long idProducto);
}
