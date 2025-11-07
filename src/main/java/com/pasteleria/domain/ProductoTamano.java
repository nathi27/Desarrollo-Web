/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.pasteleria.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity @Table(name = "producto_tamano")
public class ProductoTamano {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tamano")
    private Long idTamano;

    @ManyToOne(optional = false) @JoinColumn(name = "id_producto")
    private Producto producto;

    private String etiqueta;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

}
