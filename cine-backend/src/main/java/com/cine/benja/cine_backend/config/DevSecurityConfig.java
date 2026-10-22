package com.cine.benja.cine_backend.config;

import com.cine.benja.cine_backend.security.LocalDevelopmentUserFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/** Modo local exclusivo para probar pantallas sin credenciales Firebase. Nunca usar en producción. */
@Configuration
@ConditionalOnProperty(name = "app.auth.mode", havingValue = "dev")
public class DevSecurityConfig {
    @Bean
    public LocalDevelopmentUserFilter localDevelopmentUserFilter() {
        return new LocalDevelopmentUserFilter();
    }

    @Bean
    public FilterRegistrationBean<LocalDevelopmentUserFilter> disableContainerRegistration(
            LocalDevelopmentUserFilter filter) {
        FilterRegistrationBean<LocalDevelopmentUserFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain devFilterChain(HttpSecurity http, LocalDevelopmentUserFilter localFilter) throws Exception {
        http.cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(localFilter, AnonymousAuthenticationFilter.class);
        return http.build();
    }
}
