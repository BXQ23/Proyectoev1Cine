package com.cine.benja.cine_core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Representa la reserva de un asiento para una funcion especifica.
// Esta es la entidad "protegida": aca es donde despues va a entrar en juego
// el JWT, porque solo un usuario logueado puede crear o ver reservas.
@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacion con Funcion: cada reserva pertenece a UNA funcion,
    // pero una funcion puede tener MUCHAS reservas (una por asiento vendido)
    @ManyToOne
    @JoinColumn(name = "funcion_id") // asi se va a llamar la columna FK en la tabla
    private Funcion funcion;

    // Por ahora es un String cualquiera, pero cuando conectemos el login,
    // aca vamos a guardar el identificador que venga del JWT (email o "sub")
    private String usuario;

    private String asiento;              // ej: "B12"
    private LocalDateTime fechaReserva;  // cuando se hizo la reserva (no la funcion, ojo)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Funcion getFuncion() { return funcion; }
    public void setFuncion(Funcion funcion) { this.funcion = funcion; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getAsiento() { return asiento; }
    public void setAsiento(String asiento) { this.asiento = asiento; }

    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }
}
