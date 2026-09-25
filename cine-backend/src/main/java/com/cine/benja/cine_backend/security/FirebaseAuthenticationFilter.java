package com.cine.benja.cine_backend.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthenticationFilter(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String idToken = header.substring(7);
            try {
                FirebaseToken decoded = firebaseAuth.verifyIdToken(idToken);

                boolean esAdmin = Boolean.TRUE.equals(decoded.getClaims().get("admin"));
                AuthenticatedUser usuario = new AuthenticatedUser(decoded.getUid(), decoded.getEmail(), esAdmin);

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                if (esAdmin) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }

                FirebaseAuthenticationToken authentication = new FirebaseAuthenticationToken(usuario, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (FirebaseAuthException e) {
                // DEBUG temporal: esto nos va a decir EXACTAMENTE por que Firebase
                // esta rechazando el token. Lo sacamos despues de encontrar la causa.
                System.err.println("=== Firebase rechazo el token ===");
                System.err.println("Mensaje: " + e.getMessage());
                System.err.println("Codigo de error: " + e.getAuthErrorCode());
                e.printStackTrace();

                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                // Por si el error ni siquiera es un FirebaseAuthException (ej. problema
                // de red al verificar la firma) - antes esto se habria ido sin avisar.
                System.err.println("=== Error inesperado verificando el token ===");
                e.printStackTrace();
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}