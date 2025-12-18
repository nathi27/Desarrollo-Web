package com.pasteleria.dto;

import lombok.Data;

@Data
public class FiltroProductoDTO {
    private String categoria;
    private String nombre;
    
    
    public FiltroProductoDTO() {}
    
    
    public FiltroProductoDTO(String categoria, String nombre) {
        this.categoria = categoria;
        this.nombre = nombre;
    }
}