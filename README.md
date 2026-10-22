# Cine Nocturno

Aplicación web de cartelera y reserva de asientos. En la segunda entrega se logró desplegar el flujo principal en AWS, después de que en la primera entrega no se consiguiera dejar operativo el servidor cloud.

**Demo:** [https://cine-nocturno.duckdns.org](https://cine-nocturno.duckdns.org) — disponible mientras la instancia/laboratorio de AWS esté encendido.

## Tecnologías y por qué

- **React + Vite:** interfaz de cartelera, inicio de sesión y reservas.
- **Java 17 + Spring Boot 3:** servicios separados para API, lógica de negocio y administración de RabbitMQ.
- **Amazon RDS for MySQL:** persistencia cloud de funciones y reservas.
- **Firebase Authentication:** inicio de sesión con Google y validación de identidad.
- **RabbitMQ 3.13:** procesamiento asíncrono de eventos para tickets, notificaciones y auditoría, con colas de mensajes fallidos (DLQ).
- **Amazon EC2 + Ubuntu, Nginx y HTTPS:** alojamiento, proxy inverso y publicación segura de la web/API.
- **Docker Compose + Maven/npm:** entorno de desarrollo local reproducible.

## Arquitectura

```text
Navegador
   │ HTTPS (cine-nocturno.duckdns.org)
   ▼
Nginx en EC2 ─── sirve React/Vite compilado
   │ /api/
   ▼
cine-backend (8080) ── cine-core (8081) ── Amazon RDS MySQL
   │                         │
   └──── RabbitMQ ◄──────────┘
          ├─ cine.direct → reservas/notificaciones y tickets
          ├─ cine.topic  → auditoría
          └─ 3 colas principales + 3 DLQ

cine-rabbit-admin (8082): API de administración protegida por X-Admin-Key.
En el despliegue, los puertos internos y la administración no se publican directamente a Internet.
```

**Importante:** el entorno AWS desplegado usa Nginx como proxy inverso delante de la API. No se presenta Amazon API Gateway como un recurso desplegado en esta versión.

## Estructura del repositorio

- `cine-frontend/`: aplicación React.
- `cine-backend/`: API/BFF, autenticación, autorización y publicación de eventos.
- `cine-core/`: funciones, reservas y acceso a datos.
- `cine-rabbit-admin/`: endpoints REST protegidos para administrar la topología RabbitMQ.
- `docker-compose.yml`, `infra/rabbitmq/`: RabbitMQ para desarrollo local.
- `presentacion/`: presentación de la segunda evaluación y capturas de la consola RabbitMQ.
- `docs/RESULTADOS_SEGUNDA_ENTREGA.md`: resumen de mejoras y pruebas.

## Ejecutar localmente (Windows)

Requisitos: Java 17, Maven, Node.js/npm, Docker Desktop y Docker Compose.

1. En la raíz, copia los ejemplos de configuración:
   ```powershell
   Copy-Item .env.example .env
   Copy-Item cine-frontend/.env.example cine-frontend/.env
   ```
2. Inicia RabbitMQ con `INICIAR_RABBITMQ.bat` o `docker compose up -d`.
3. En terminales separadas, ejecuta `INICIAR_CORE.bat`, `INICIAR_BACKEND_LOCAL.bat`, `INICIAR_RABBIT_ADMIN.bat` y `INICIAR_FRONTEND.bat`.
4. Abre la URL que indique Vite, `http://localhost:3000`.

El modo `APP_AUTH_MODE=dev` simula un usuario para pruebas locales. **No lo uses en AWS.** Para ejecutar con Firebase real, configura `FIREBASE_CREDENTIALS_PATH` con un JSON de servicio almacenado fuera del repositorio.

Consolas RabbitMQ locales: `http://localhost:15672` y `http://localhost:15673`. Usuario/clave predeterminados en `.env.example` son únicamente de desarrollo y deben cambiarse si se reutiliza el entorno.

## Pruebas y resultados verificados en AWS

- El sitio sirve por HTTPS y HTTP redirige a HTTPS.
- El endpoint público de cartelera responde correctamente.
- El acceso a reservas sin autenticación devuelve `401`; el inicio de sesión y los flujos de reserva/cancelación se probaron desde la web.
- La disponibilidad de asientos baja al reservar y se recupera al cancelar.
- Los tres servicios Java arrancan automáticamente tras reiniciar EC2.
- Se comprobó el clúster RabbitMQ con dos nodos, sus exchanges y las seis colas principales/DLQ.
- Se enviaron mensajes sintéticos con formato inválido a los consumidores de tickets, notificaciones y auditoría; los tres fueron rechazados y enviados a su DLQ correspondiente. Después se retiraron los mensajes de prueba y se verificó que las seis colas quedaran en cero.

Las capturas y una descripción resumida están en `presentacion/evidencias/` y `docs/RESULTADOS_SEGUNDA_ENTREGA.md`.

## Seguridad para GitHub

No subas `.env`, contraseñas de RDS, JSON de Firebase Admin, tokens DuckDNS, claves de administración ni archivos de configuración privados de la instancia. El repositorio incluye `.env.example` con valores de desarrollo. Las claves de producción se configuran fuera del código.

## Alcance conocido

Las notificaciones y la generación de tickets se representan mediante eventos/logs de demostración; no hay un proveedor externo de correo ni un sistema de emisión de tickets conectado. Las pruebas DLQ documentadas verifican el rechazo de mensajes con formato inválido; no equivalen a una prueba completa de todos los escenarios de reintento de negocio.
