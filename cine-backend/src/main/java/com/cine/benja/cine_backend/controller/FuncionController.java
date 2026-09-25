package com.cine.benja.cine_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

// Este es el UNICO endpoint publico del proyecto (sin login). El BFF lo deja
// pasar sin pedir JWT (ver SecurityConfig: /api/public/** -> permitAll) y
// simplemente reenvia la consulta a cine-core, que es quien tiene la BD real.
@RestController
@RequestMapping("/api/public/funciones")
public class FuncionController {

    private final RestTemplate restTemplate;
    private final String coreUrl;

    public FuncionController(RestTemplate restTemplate,
                              @Value("${core.service.url}") String coreUrl) {
        this.restTemplate = restTemplate;
        this.coreUrl = coreUrl;
    }

    @GetMapping
    public ResponseEntity<Object> listar() {
        return restTemplate.getForEntity(coreUrl + "/internal/funciones", Object.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> obtener(@PathVariable Long id) {
        try {
            return restTemplate.getForEntity(coreUrl + "/internal/funciones/" + id, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
