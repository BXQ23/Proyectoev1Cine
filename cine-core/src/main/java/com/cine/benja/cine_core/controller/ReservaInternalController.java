package com.cine.benja.cine_core.controller;

import com.cine.benja.cine_core.model.Reserva;
import com.cine.benja.cine_core.repository.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

// Igual que FuncionInternalController: sin seguridad aca. El BFF ya valido
// el JWT y el scope antes de reenviar la peticion hasta aca.
@RestController
@RequestMapping("/internal/reservas")
public class ReservaInternalController {

    private final ReservaRepository reservaRepository;

    public ReservaInternalController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @GetMapping
    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtener(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody Reserva reserva) {
        reserva.setFechaReserva(LocalDateTime.now());
        Reserva guardada = reservaRepository.save(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        if (!reservaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reservaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
