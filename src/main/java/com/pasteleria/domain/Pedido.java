package com.pasteleria.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    private Long idUsuario; // Vinculamos con el usuario que compra
    private LocalDateTime fecha;
    private Double total;
    private String metodoPago; // Aquí guardaremos "Sinpe", "Tarjeta", etc.
    private String estado; // "Pendiente", "Pagado", etc.

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;
}
