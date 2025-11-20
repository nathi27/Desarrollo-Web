/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.pasteleria.repository;

import com.pasteleria.domain.ExpiracionTokenContrasena;
import com.pasteleria.domain.Usuario;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpiracionTokenContrasenaRepository extends JpaRepository<ExpiracionTokenContrasena, Long> {
    Optional<ExpiracionTokenContrasena>
    findTopByUsuarioAndCodigoAndUsadoAndExpiraEnAfterOrderByExpiraEnDesc(
                Usuario usuario,
                String codigo,
                Integer usado,
                LocalDateTime ahora
        );
}
