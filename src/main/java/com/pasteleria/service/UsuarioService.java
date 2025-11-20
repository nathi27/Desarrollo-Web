/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pasteleria.service;

import com.pasteleria.domain.Usuario;
import com.pasteleria.domain.ExpiracionTokenContrasena;
import com.pasteleria.dto.RegistroUsuarioForm;
import com.pasteleria.repository.UsuarioRepository;
import com.pasteleria.repository.ExpiracionTokenContrasenaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service

public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final ExpiracionTokenContrasenaRepository tokenRepository;
    private final Random random = new Random();

    public UsuarioService(UsuarioRepository usuarioRepository,
                          ExpiracionTokenContrasenaRepository tokenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
    }

    // ======================================================
    // HU1: Registro de usuarios
    // ======================================================

    /**
     * Verifica si ya existe un usuario con ese correo.
     */
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    /**
     * Regla de contraseña (HU1):
     *  - mínimo 8 caracteres
     *  - al menos UNA mayúscula
     */
    public boolean passwordValida(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean tieneMayuscula = password.chars().anyMatch(Character::isUpperCase);
        return tieneMayuscula;
    }

    /**
     * Registra un nuevo usuario en la BD a partir del formulario.
     */
    @Transactional
    public Usuario registrarNuevoUsuario(RegistroUsuarioForm form) {
        Usuario usuario = new Usuario();
        usuario.setNombre(form.getNombre());
        // Guardamos el correo en minúsculas para consistencia
        usuario.setCorreo(form.getCorreo().toLowerCase());
        usuario.setPasswordHash(hashPassword(form.getPassword()));
        usuario.setActivo(Boolean.TRUE); // importante que no quede null
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaModificacion(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    // ======================================================
    // HU13: Recuperación / cambio de contraseña
    // ======================================================

    /**
     * Genera un código de 6 dígitos, lo guarda en la tabla
     * expiracion_token_contraseña y "simula" el envío al correo.
     *
     * Retorna el código solo para pruebas (en producción no se devolvería).
     */
    @Transactional
    public String generarCodigoRecuperacion(String correo) {
        Optional<Usuario> opt = usuarioRepository.findByCorreo(correo);
        if (opt.isEmpty()) {
            // Por seguridad, no indicamos si existe o no el correo.
            return null;
        }

        Usuario usuario = opt.get();

        // Generamos código 6 dígitos, ej: 034921
        String codigo = String.format("%06d", random.nextInt(1_000_000));
        LocalDateTime expira = LocalDateTime.now().plusMinutes(15);

        ExpiracionTokenContrasena token = new ExpiracionTokenContrasena();
        token.setUsuario(usuario);
        token.setCodigo(codigo);
        token.setExpiraEn(expira);
        token.setUsado(0); // 0 = no usado
        token.setFechaCreacion(LocalDateTime.now());
        token.setFechaModificacion(LocalDateTime.now());

        tokenRepository.save(token);

        // Aquí iría el envío real por correo (SMTP, servicio externo, etc.)
        // Para pruebas, lo mostramos en consola:
        System.out.println("Código de recuperación para " + correo + ": " + codigo);

        return codigo;
    }

    /**
     * Valida que el código:
     *  - exista para ese usuario
     *  - no esté usado (usado = 0)
     *  - no esté vencido (expira_en > ahora)
     */
    @Transactional
    public boolean validarCodigo(String correo, String codigo) {
        Optional<Usuario> opt = usuarioRepository.findByCorreo(correo);
        if (opt.isEmpty()) {
            return false;
        }
        Usuario usuario = opt.get();

        Optional<ExpiracionTokenContrasena> tokenOpt =
                tokenRepository.findTopByUsuarioAndCodigoAndUsadoAndExpiraEnAfterOrderByExpiraEnDesc(
                        usuario,
                        codigo,
                        0, // buscamos tokens NO usados
                        LocalDateTime.now()
                );

        if (tokenOpt.isEmpty()) {
            return false;
        }

        // Marcamos el token como usado
        ExpiracionTokenContrasena token = tokenOpt.get();
        token.setUsado(1); // 1 = usado
        token.setFechaModificacion(LocalDateTime.now());
        tokenRepository.save(token);

        return true;
    }

    /**
     * Cambia la contraseña de un usuario según su correo.
     */
    @Transactional
    public boolean cambiarPassword(String correo, String nuevaPass) {
        Optional<Usuario> opt = usuarioRepository.findByCorreo(correo);
        if (opt.isEmpty()) {
            return false;
        }

        Usuario usuario = opt.get();
        usuario.setPasswordHash(hashPassword(nuevaPass));
        usuario.setFechaModificacion(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return true;
    }

    // ======================================================
    // Utilidad: Hash de contraseña con SHA-256 (simple)
    // ======================================================

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo inicializar SHA-256", e);
        }
    }
    
}
