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
import com.pasteleria.dto.ActualizarCuentaForm;

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

    
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    
    public boolean passwordValida(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean tieneMayuscula = password.chars().anyMatch(Character::isUpperCase);
        return tieneMayuscula;
    }

    
    @Transactional
    public Usuario registrarNuevoUsuario(RegistroUsuarioForm form) {
        Usuario usuario = new Usuario();
        usuario.setNombre(form.getNombre());
        
        usuario.setCorreo(form.getCorreo().toLowerCase());
        usuario.setPasswordHash(hashPassword(form.getPassword()));
        usuario.setActivo(Boolean.TRUE); 
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaModificacion(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    
    @Transactional
    public String generarCodigoRecuperacion(String correo) {
        Optional<Usuario> opt = usuarioRepository.findByCorreo(correo);
        if (opt.isEmpty()) {
            
            return null;
        }

        Usuario usuario = opt.get();

        
        String codigo = String.format("%06d", random.nextInt(1_000_000));
        LocalDateTime expira = LocalDateTime.now().plusMinutes(15);

        ExpiracionTokenContrasena token = new ExpiracionTokenContrasena();
        token.setUsuario(usuario);
        token.setCodigo(codigo);
        token.setExpiraEn(expira);
        token.setUsado(0); 
        token.setFechaCreacion(LocalDateTime.now());
        token.setFechaModificacion(LocalDateTime.now());

        tokenRepository.save(token);

        
        System.out.println("Código de recuperación para " + correo + ": " + codigo);

        return codigo;
    }

    
    @Transactional
    public boolean validarCodigo(String correo, String codigo) {
        Optional<Usuario> opt = usuarioRepository.findByCorreo(correo);
        if (opt.isEmpty()) {
            return false;
        }
        Usuario usuario = opt.get();

        Optional<ExpiracionTokenContrasena> tokenOpt
                = tokenRepository.findTopByUsuarioAndCodigoAndUsadoAndExpiraEnAfterOrderByExpiraEnDesc(
                        usuario,
                        codigo,
                        0, 
                        LocalDateTime.now()
                );

        if (tokenOpt.isEmpty()) {
            return false;
        }

        
        ExpiracionTokenContrasena token = tokenOpt.get();
        token.setUsado(1); 
        token.setFechaModificacion(LocalDateTime.now());
        tokenRepository.save(token);

        return true;
    }

  
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

   
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        if (correo == null) {
            return Optional.empty();
        }
        String correoNormalizado = correo.trim().toLowerCase();
        return usuarioRepository.findByCorreo(correoNormalizado);
    }

    public boolean correoEnUsoPorOtroUsuario(String correo, Long idUsuario) {
        if (correo == null || idUsuario == null) {
            return false;
        }
        String correoNormalizado = correo.trim().toLowerCase();
        return usuarioRepository.existsByCorreoAndIdUsuarioNot(correoNormalizado, idUsuario);
    }

    public boolean passwordActualCorrecta(Usuario usuario, String passwordActual) {
        if (usuario == null || passwordActual == null) {
            return false;
        }
        String actual = passwordActual.trim();
        if (actual.length() == 0) {
            return false;
        }
        String hashIngresado = hashPassword(actual);
        String hashGuardado = usuario.getPasswordHash();
        if (hashGuardado == null) {
            return false;
        }
        return hashGuardado.equals(hashIngresado);
    }

    @Transactional
    public Usuario aplicarCambiosCuenta(Usuario usuario, ActualizarCuentaForm form) {
        

        usuario.setNombre(form.getNombre() == null ? "" : form.getNombre().trim());
        usuario.setCorreo(form.getCorreo() == null ? "" : form.getCorreo().trim().toLowerCase());

        String nueva = form.getNuevaPassword();
        if (nueva != null) {
            String n = nueva.trim();
            if (n.length() > 0) {
                usuario.setPasswordHash(hashPassword(n));
            }
        }

        usuario.setFechaModificacion(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    
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
