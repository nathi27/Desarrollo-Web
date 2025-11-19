/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pasteleria.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String conservacion;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    private Boolean activo;

    private Integer stock;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @OneToMany(mappedBy = "producto")
    private List<ProductoTamano> tamanos;

    @OneToMany(mappedBy = "producto")
    private List<ProductoSabor> sabores;

    @OneToMany(mappedBy = "producto")
    private List<ProductoIngrediente> ingredientes;

}
