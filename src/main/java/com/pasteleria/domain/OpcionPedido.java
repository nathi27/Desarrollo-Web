/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pasteleria.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "opcion_pedido")
public class OpcionPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_opcion")
    private Long idOpcion;

    private String categoria; // "producto", "sabor_bizcocho", "sabor_relleno", "tamano"
    private String valor; // "Pastel", "Chocolate", etc.
    private String descripcion;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal precioAdicional;

    private Boolean activo;
}