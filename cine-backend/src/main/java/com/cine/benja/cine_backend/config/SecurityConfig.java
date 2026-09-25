package com.cine.benja.cine_backend.config;

import com.cine.benja.cine_backend.security.FirebaseAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

// Con Firebase, este backend sigue siendo un "Resource Server" puro: nunca
// valida usuario/contraseña ni firma tokens - eso lo hace Firebase Authentication.
// Aca solo decidimos QUE rutas necesitan un token valido, y dejamos que
// FirebaseAuthenticationFilter (ver paquete security) se encargue de verificarlo.
@Configuration
public class SecurityConfig {

    private final FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    public SecurityConfig(FirebaseAuthenticationFilter firebaseAuthenticationFilter) {
        this.firebaseAuthenticationFilter = firebaseAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Engancha el CorsConfigurationSource bean (ver CorsConfig.java) DENTRO
            // de la cadena de Security - asi hasta una respuesta 401 lleva los
            // headers de CORS correctos, y el navegador no lo confunde con un
            // bloqueo de CORS.
            .cors(cors -> {})
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Unico endpoint publico del proyecto (pauta EV1)
                .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                // Todo lo demas de /api/reservas/** exige estar logueado (con Firebase).
                // La diferencia entre "puede ver todo" (ADMIN) y "solo lo propio" (USER)
                // se resuelve dentro de ReservaController, no aca - por eso los 403
                // de ownership los tira el controller, no esta config.
                .requestMatchers("/api/reservas/**").authenticated()
                .anyRequest().authenticated()
            )
            // Sin token (o token invalido) en una ruta protegida -> 401 explicito,
            // en vez del comportamiento por defecto de Spring Security (redirigir a un login form).
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            // Nuestro filtro corre ANTES del de usuario/password de Spring (que ni usamos,
            // pero hay que decirle donde engancharse en la cadena de filtros).
            .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
