package com.pasteleria.repository;

import com.pasteleria.domain.Resena;
import com.pasteleria.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    
    List<Resena> findByProductoIdProducto(Long idProducto);
    
    List<Resena> findByProductoIdProductoAndAprobadaTrue(Long idProducto);
    
    List<Resena> findByUsuarioIdUsuario(Long idUsuario);
    
    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.producto.idProducto = :productoId AND r.aprobada = true")
    Double findPromedioCalificacionByProducto(@Param("productoId") Long productoId);
    
    @Query("SELECT COUNT(r) FROM Resena r WHERE r.producto.idProducto = :productoId AND r.aprobada = true")
    Integer countResenasAprobadasByProducto(@Param("productoId") Long productoId);
    
    boolean existsByUsuarioIdUsuarioAndProductoIdProducto(Long idUsuario, Long idProducto);
}