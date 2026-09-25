package com.cine.benja.cine_core.repository;

import com.cine.benja.cine_core.model.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;

// No necesitamos escribir ni un solo metodo aca.
// Al extender JpaRepository, Spring Data ya nos regala gratis:
// findAll(), findById(), save(), deleteById(), etc.
public interface FuncionRepository extends JpaRepository<Funcion, Long> {
}
