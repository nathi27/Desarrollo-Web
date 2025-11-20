package com.pasteleria.service;

import lombok.Data;
import java.util.List;

@Data
public class CompraDto {
    private String metodoPago;
    private Double total;
    private List<ItemCarritoDto> items;

    @Data
    public static class ItemCarritoDto {
        private Long id;
        private String nombre;
        private Double precio;
        private Integer cantidad;
    }
}
