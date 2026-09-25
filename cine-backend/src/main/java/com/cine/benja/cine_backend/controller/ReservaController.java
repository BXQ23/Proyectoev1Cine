package com.cine.benja.cine_backend.controller;

import com.cine.benja.cine_backend.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

// Rutas protegidas (ver SecurityConfig): TODAS requieren estar logueado con Firebase.
// La diferencia entre "usuario normal" y "admin" no se resuelve con anotaciones aca,
// se resuelve a mano abajo - los ADMIN (custom claim "admin": true en Firebase) ven
// y cancelan cualquier reserva; un usuario normal solo ve/cancela las suyas.
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final RestTemplate restTemplate;
    private final String coreUrl;

    public ReservaController(RestTemplate restTemplate,
                              @Value("${core.service.url}") String coreUrl) {
        this.restTemplate = restTemplate;
        this.coreUrl = coreUrl;
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @GetMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> listar(@AuthenticationPrincipal AuthenticatedUser usuario,
                                          Authentication authentication) {
        ResponseEntity<List> respuesta = restTemplate.getForEntity(coreUrl + "/internal/reservas", List.class);
        List<Map<String, Object>> reservas = respuesta.getBody();

        if (esAdmin(authentication) || reservas == null) {
            return ResponseEntity.ok(reservas);
        }

        // Un usuario normal solo ve sus propias reservas (comparando por email)
        List<Map<String, Object>> propias = reservas.stream()
                .filter(r -> usuario.email() != null && usuario.email().equals(r.get("usuario")))
                .toList();
        return ResponseEntity.ok(propias);
    }

    @GetMapping("/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> obtener(@PathVariable Long id,
                                           @AuthenticationPrincipal AuthenticatedUser usuario,
                                           Authentication authentication) {
        Map<String, Object> reserva;
        try {
            ResponseEntity<Map> respuesta = restTemplate.getForEntity(coreUrl + "/internal/reservas/" + id, Map.class);
            reserva = respuesta.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        }

        if (!esAdmin(authentication) && !usuario.email().equals(reserva.get("usuario"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reserva);
    }

    // Cualquier usuario autenticado puede reservar - se guarda con SU propio email
    // (sacado del token ya verificado), nunca con lo que mande el frontend en el body.
    @PostMapping
    public ResponseEntity<Object> reservar(@RequestBody Map<String, Object> body,
                                            @AuthenticationPrincipal AuthenticatedUser usuario) {
        body.put("usuario", usuario.email());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);
        return restTemplate.postForEntity(coreUrl + "/internal/reservas", request, Object.class);
    }

    @DeleteMapping("/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Void> cancelar(@PathVariable Long id,
                                          @AuthenticationPrincipal AuthenticatedUser usuario,
                                          Authentication authentication) {
        // Antes de cancelar, hay que saber de quien es la reserva - por eso el GET previo.
        if (!esAdmin(authentication)) {
            Map<String, Object> reserva;
            try {
                ResponseEntity<Map> respuesta = restTemplate.getForEntity(coreUrl + "/internal/reservas/" + id, Map.class);
                reserva = respuesta.getBody();
            } catch (HttpClientErrorException.NotFound e) {
                return ResponseEntity.notFound().build();
            }
            if (!usuario.email().equals(reserva.get("usuario"))) {
                // Logueado, token valido, pero intentando cancelar una reserva ajena -> 403
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        try {
            restTemplate.delete(coreUrl + "/internal/reservas/" + id);
            return ResponseEntity.noContent().build();
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        }
    }
}
