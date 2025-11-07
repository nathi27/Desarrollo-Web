/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.pasteleria.repository;

import com.pasteleria.domain.ExpiracionTokenContrasena;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpiracionTokenContrasenaRepository extends JpaRepository<ExpiracionTokenContrasena, Long> {
    Optional<ExpiracionTokenContrasena>
    findTopByUsuario_CorreoAndCodigoAndUsadoFalseOrderByIdTokenDesc(String correo, String codigo);
}
