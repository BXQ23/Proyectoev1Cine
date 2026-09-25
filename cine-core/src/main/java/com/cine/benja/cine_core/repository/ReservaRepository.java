package com.cine.benja.cine_core.repository;

import com.cine.benja.cine_core.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

// Mismo caso: heredamos todo el CRUD basico sin escribir codigo.
// Mas adelante, si necesitamos "buscar reservas por usuario",
// aca agregariamos algo como: List<Reserva> findByUsuario(String usuario);
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
