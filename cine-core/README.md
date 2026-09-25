# cine-core (microservicio de negocio)

Microservicio de negocio del proyecto EV1 DSY1107 — dominio "Cine".

Contiene toda la lógica y persistencia real: entidades `Funcion` y `Reserva`,
repositorios JPA, y la base de datos (H2 por defecto, MySQL listo para cuando
migres). **Este servicio no sabe nada de JWT, scopes, ni usuarios** — esa
responsabilidad es del BFF (`cine-backend`), que es el único que debería
poder llamarlo.

## Cómo correrlo

1. Ábrelo en VS Code (carpeta separada de `cine-backend`).
2. Corre `CineCoreApplication.java`.
3. Queda arriba en `http://localhost:8081`.

Por defecto usa H2 en memoria — se recarga con los datos de `data.sql` cada vez
que reinicias. Consola web: `http://localhost:8081/h2-console`
(JDBC URL: `jdbc:h2:mem:cinedb`, user `sa`, sin password).

## Endpoints (solo para el BFF, no para el frontend directo)

| Método | Ruta |
|---|---|
| GET | `/internal/funciones` |
| GET | `/internal/funciones/{id}` |
| GET | `/internal/reservas` |
| GET | `/internal/reservas/{id}` |
| POST | `/internal/reservas` |
| DELETE | `/internal/reservas/{id}` |

Se llaman `/internal/...` a propósito, para dejar claro que no son la API
pública del sistema — esa es la que expone `cine-backend`.

## Migrar a MySQL

Igual que antes: en `application.properties`, comenta el bloque H2, descomenta
el bloque MySQL, pon tu password, y agrega `spring.sql.init.mode=always` (Spring
solo corre `data.sql` automático en bases embebidas como H2, no en MySQL).

## Al desplegar en AWS (más adelante)

Este servicio va en su propia instancia EC2 (o el mismo contenedor que el BFF,
en puertos distintos), pero su security group **no debe permitir tráfico desde
internet** — solo desde la instancia donde corre `cine-backend`. Todo el tráfico
externo pasa por el API Gateway → BFF → aquí.
