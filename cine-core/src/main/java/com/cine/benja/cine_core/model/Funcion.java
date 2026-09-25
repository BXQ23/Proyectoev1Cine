package com.cine.benja.cine_core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Esta clase representa una funcion de cine en la base de datos.
// Cada instancia = una fila en la tabla "funcion" que Spring crea automaticamente.
@Entity
public class Funcion {

    // El id se genera solo (autoincremental), no lo seteamos nosotros nunca
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pelicula;             // nombre de la peli, ej: "Dune 2"
    private String sala;                 // sala donde se exhibe, ej: "Sala 3"
    private LocalDateTime horario;       // fecha y hora de la funcion
    private Integer asientosDisponibles; // cuantos asientos quedan libres

    // Getters y setters: Spring los necesita para leer/escribir estos datos
    // cuando convierte el objeto a JSON (o viceversa) en las peticiones HTTP
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPelicula() { return pelicula; }
    public void setPelicula(String pelicula) { this.pelicula = pelicula; }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public LocalDateTime getHorario() { return horario; }
    public void setHorario(LocalDateTime horario) { this.horario = horario; }

    public Integer getAsientosDisponibles() { return asientosDisponibles; }
    public void setAsientosDisponibles(Integer asientosDisponibles) { this.asientosDisponibles = asientosDisponibles; }
}
