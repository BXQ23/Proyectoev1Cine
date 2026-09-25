package com.cine.benja.cine_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// Sin esto, el navegador bloquea las llamadas del frontend (localhost:3000/5173/4200)
// hacia este backend (localhost:8080) por ser origenes distintos.
//
// OJO: esta como CorsConfigurationSource (no como WebMvcConfigurer) a proposito.
// Con Spring Security activo, el filtro de seguridad intercepta el request ANTES
// de que la configuracion de CORS a nivel de MVC llegue a aplicarse - por eso
// un 401 salia sin los headers de CORS y el navegador lo reportaba como error
// de CORS en vez de mostrar el 401 real. Registrando esto como bean, SecurityConfig
// lo engancha directo en su propia cadena de filtros (ver .cors(...) alla).
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
