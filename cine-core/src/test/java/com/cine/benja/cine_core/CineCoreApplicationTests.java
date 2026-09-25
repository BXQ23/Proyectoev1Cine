package com.cine.benja.cine_core;

import com.cine.benja.cine_core.model.Funcion;
import com.cine.benja.cine_core.repository.FuncionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pruebas basicas del microservicio de negocio: como este servicio ya no
// valida JWT (esa parte quedo en el BFF), aca solo probamos que el CRUD
// en si funcione bien contra la base de datos.
@SpringBootTest
@AutoConfigureMockMvc
class CineCoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FuncionRepository funcionRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void listarFunciones_devuelveLasCargadasPorDataSql() throws Exception {
        mockMvc.perform(get("/internal/funciones"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obtenerFuncionInexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/internal/funciones/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void crearYCancelarReserva_flujoCompleto() throws Exception {
        Funcion funcion = new Funcion();
        funcion.setPelicula("Pelicula de prueba");
        funcion.setSala("Sala 9");
        funcion.setHorario(LocalDateTime.now().plusDays(1));
        funcion.setAsientosDisponibles(50);
        Funcion guardada = funcionRepository.save(funcion);

        String body = """
            {"funcion": {"id": %d}, "usuario": "test@test.com", "asiento": "C7"}
            """.formatted(guardada.getId());

        String response = mockMvc.perform(post("/internal/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        // Extrae el id devuelto para poder cancelarla despues (chequeo simple sin libreria extra)
        Long reservaId = Long.valueOf(response.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(delete("/internal/reservas/" + reservaId))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/internal/reservas/" + reservaId))
            .andExpect(status().isNotFound());
    }
}
