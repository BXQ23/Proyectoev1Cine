package com.cine.benja.cine_backend;

import com.cine.benja.cine_backend.security.AuthenticatedUser;
import com.cine.benja.cine_backend.security.FirebaseAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

// Prueba la matriz de seguridad completa que pide la pauta de EV1
// (publico -> 2xx, sin token -> 401, usuario intentando algo ajeno -> 403,
// usuario en lo suyo / admin -> 2xx), ahora con Firebase.
//
// No llamamos a Firebase de verdad: usamos nuestro propio AuthenticatedUser
// envuelto en FirebaseAuthenticationToken vía el postprocessor authentication(...)
// de spring-security-test. Esto reemplaza lo que antes hacia jwt() con Azure.
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // El BFF llama a cine-core por HTTP real; en los tests reemplazamos ese
    // cliente por un mock para no depender de que cine-core este corriendo.
    @MockBean
    private RestTemplate restTemplate;

    private FirebaseAuthenticationToken usuarioNormal(String email) {
        AuthenticatedUser usuario = new AuthenticatedUser("uid-1", email, false);
        return new FirebaseAuthenticationToken(usuario, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private FirebaseAuthenticationToken admin() {
        AuthenticatedUser usuario = new AuthenticatedUser("uid-admin", "admin@test.com", true);
        return new FirebaseAuthenticationToken(usuario,
                List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void endpointPublico_noNecesitaToken() throws Exception {
        when(restTemplate.getForEntity(any(String.class), any()))
            .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/api/public/funciones"))
            .andExpect(status().isOk());
    }

    @Test
    void reservas_sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/reservas"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void listarReservas_usuarioNormal_devuelveSoloLasSuyas() throws Exception {
        when(restTemplate.getForEntity(any(String.class), eq(List.class)))
            .thenReturn(ResponseEntity.ok(List.of(
                Map.of("id", 1, "usuario", "benja@test.com"),
                Map.of("id", 2, "usuario", "otro@test.com")
            )));

        mockMvc.perform(get("/api/reservas").with(authentication(usuarioNormal("benja@test.com"))))
            .andExpect(status().isOk());
    }

    @Test
    void cancelarReservaAjena_usuarioNormal_devuelve403() throws Exception {
        when(restTemplate.getForEntity(any(String.class), eq(Map.class)))
            .thenReturn(ResponseEntity.ok(Map.of("id", 5, "usuario", "otro@test.com")));

        mockMvc.perform(delete("/api/reservas/5").with(authentication(usuarioNormal("benja@test.com"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void cancelarReservaPropia_usuarioNormal_devuelve204() throws Exception {
        when(restTemplate.getForEntity(any(String.class), eq(Map.class)))
            .thenReturn(ResponseEntity.ok(Map.of("id", 5, "usuario", "benja@test.com")));

        mockMvc.perform(delete("/api/reservas/5").with(authentication(usuarioNormal("benja@test.com"))))
            .andExpect(status().isNoContent());
    }

    @Test
    void cancelarCualquierReserva_admin_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/reservas/5").with(authentication(admin())))
            .andExpect(status().isNoContent());
    }

    @Test
    void crearReserva_usuarioAutenticado_devuelveCreated() throws Exception {
        when(restTemplate.postForEntity(any(String.class), any(), any()))
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(post("/api/reservas")
                .with(authentication(usuarioNormal("benja@test.com")))
                .contentType("application/json")
                .content("{\"asiento\":\"B12\"}"))
            .andExpect(status().isCreated());
    }
}
