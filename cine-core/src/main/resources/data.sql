-- Estos datos se insertan automaticamente cada vez que arranca la app con H2,
-- asi la cartelera no aparece vacia cuando pruebes el endpoint publico.
INSERT INTO funcion (pelicula, sala, horario, asientos_disponibles) VALUES
('Dune: Parte Dos', 'Sala 1', '2026-09-15 20:00:00', 40),
('Inside Out 3', 'Sala 2', '2026-09-15 18:30:00', 25),
('Deadpool & Wolverine', 'Sala 3', '2026-09-16 22:00:00', 30);
