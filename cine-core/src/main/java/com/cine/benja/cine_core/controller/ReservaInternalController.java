package com.cine.benja.cine_core.controller;

import com.cine.benja.cine_core.model.Reserva;
import com.cine.benja.cine_core.repository.ReservaRepository;
import com.cine.benja.cine_core.messaging.ReservaEventPublisher;
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
    private final ReservaEventPublisher eventPublisher;

    public ReservaInternalController(ReservaRepository reservaRepository, ReservaEventPublisher eventPublisher) {
        this.reservaRepository = reservaRepository;
        this.eventPublisher = eventPublisher;
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
        eventPublisher.publicar("RESERVA_CREADA", guardada.getId(), guardada.getUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        var reserva = reservaRepository.findById(id);
        if (reserva.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reservaRepository.deleteById(id);
        eventPublisher.publicar("RESERVA_CANCELADA", id, reserva.get().getUsuario());
        return ResponseEntity.noContent().build();
    }
}
