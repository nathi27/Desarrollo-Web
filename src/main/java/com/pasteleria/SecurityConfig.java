/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pasteleria;

import com.pasteleria.domain.Usuario;
import com.pasteleria.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.authentication.AuthenticationDetailsSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
     @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider nombreCorreoAuthenticationProvider,
            AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource
    ) throws Exception {

        http.authenticationProvider(nombreCorreoAuthenticationProvider);

        http
            .authorizeHttpRequests(auth -> auth
                
                .requestMatchers(
                        "/login",
                        "/registro",
                        "/recuperar/**",
                        "/webjars/**",
                        "/error"
                ).permitAll()
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("correo")
                .passwordParameter("password")
                .authenticationDetailsSource(authenticationDetailsSource)
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        
        return http.build();
    }

    @Bean
    AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return NombreCorreoWebAuthenticationDetails::new;
    }

    @Bean
    AuthenticationProvider nombreCorreoAuthenticationProvider(UsuarioRepository usuarioRepository) {
        return new NombreCorreoAuthenticationProvider(usuarioRepository);
    }
}


class NombreCorreoWebAuthenticationDetails extends WebAuthenticationDetails {
    private final String nombre;

    public NombreCorreoWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.nombre = request.getParameter("nombre");
    }

    public String getNombre() {
        return nombre;
    }
}


class NombreCorreoAuthenticationProvider implements AuthenticationProvider {

    private final UsuarioRepository usuarioRepository;

    public NombreCorreoAuthenticationProvider(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String correo = Optional.ofNullable(authentication.getName())
                .orElse("")
                .trim()
                .toLowerCase(Locale.ROOT);

        String rawPassword = String.valueOf(authentication.getCredentials());

        String nombreIngresado = null;
        Object details = authentication.getDetails();
        if (details instanceof NombreCorreoWebAuthenticationDetails d) {
            nombreIngresado = d.getNombre();
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (usuario.getActivo() != null && !usuario.getActivo()) {
            throw new DisabledException("Usuario inactivo");
        }

        
        if (nombreIngresado == null || nombreIngresado.isBlank()) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        if (!normalizarTexto(nombreIngresado).equals(normalizarTexto(usuario.getNombre()))) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        
        String hashIngresado = sha256Hex(rawPassword);
        String hashGuardado = Optional.ofNullable(usuario.getPasswordHash()).orElse("");
        if (!hashIngresado.equalsIgnoreCase(hashGuardado)) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(usuario.getCorreo(), null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private static String normalizarTexto(String s) {
        String t = s == null ? "" : s.trim();
        t = t.replaceAll("\\s+", " ");
        return t.toLowerCase(Locale.ROOT);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((input == null ? "" : input).getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo inicializar SHA-256", e);
        }
    }
    
}
