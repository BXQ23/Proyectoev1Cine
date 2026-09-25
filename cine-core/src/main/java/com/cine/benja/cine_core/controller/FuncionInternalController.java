package com.cine.benja.cine_core.controller;

import com.cine.benja.cine_core.model.Funcion;
import com.cine.benja.cine_core.repository.FuncionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Rutas "internas": este microservicio NO sabe nada de JWT, scopes ni usuarios.
// En produccion, el security group de AWS debe permitir que SOLO el BFF
// le pueda hablar a este servicio (nunca expuesto directo a internet).
@RestController
@RequestMapping("/internal/funciones")
public class FuncionInternalController {

    private final FuncionRepository funcionRepository;

    public FuncionInternalController(FuncionRepository funcionRepository) {
        this.funcionRepository = funcionRepository;
    }

    @GetMapping
    public List<Funcion> listar() {
        return funcionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcion> obtener(@PathVariable Long id) {
        return funcionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
