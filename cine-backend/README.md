# cine-backend

Servicio Spring Boot que actúa como API/BFF entre el frontend y `cine-core`.

Responsabilidades principales:
- Publicar los endpoints de cartelera y reservas.
- Validar ID tokens de Firebase Authentication en modo `firebase`.
- Aplicar autorización de reservas y reenviar operaciones internas a `cine-core`.
- Publicar eventos en RabbitMQ para notificaciones y tickets.

Puerto local: `8080`. Para iniciar en Windows, usa `INICIAR_BACKEND_LOCAL.bat` desde la raíz del repositorio (modo simulado solo para desarrollo). En AWS se ejecuta en modo Firebase, con las credenciales administrativas configuradas fuera del repositorio.

Consulta el [README principal](../README.md) para arquitectura, configuración y pruebas.
