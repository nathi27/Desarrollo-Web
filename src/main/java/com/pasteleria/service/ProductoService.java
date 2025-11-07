/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.pasteleria.service;

import com.pasteleria.domain.Producto;
import com.pasteleria.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository prodRepo;
    private final ProductoTamanoRepository tamRepo;
    private final ProductoSaborRepository sabRepo;
    private final ProductoIngredienteRepository ingRepo;

    public ProductoService(ProductoRepository p, ProductoTamanoRepository t, ProductoSaborRepository s, ProductoIngredienteRepository i){
        this.prodRepo=p; this.tamRepo=t; this.sabRepo=s; this.ingRepo=i;
    }

    public List<Producto> listarActivos(){ return prodRepo.findByActivoTrue(); }
    public Producto ver(Long id){ return prodRepo.findById(id).orElse(null); }
    public List<?> tamanos(Long id){ return tamRepo.findByProductoIdProducto(id); }
    public List<?> sabores(Long id){ return sabRepo.findByProductoIdProducto(id); }
    public List<?> ingredientes(Long id){ return ingRepo.findByProductoIdProducto(id); }

}
