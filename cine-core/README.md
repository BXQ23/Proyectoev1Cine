# cine-core

Microservicio Spring Boot de funciones y reservas. Gestiona las reglas de negocio y la persistencia.

- Puerto local: `8081`.
- Desarrollo local: H2 en memoria y datos de ejemplo.
- Despliegue cloud: perfil `aws` con Amazon RDS for MySQL y variables de entorno.
- Endpoints `/internal/...`: pensados para ser llamados desde `cine-backend`, no desde el navegador.
- Publica eventos de auditoría en RabbitMQ.

En Windows, ejecuta `INICIAR_CORE.bat` desde la raíz. Consulta el [README principal](../README.md) para instrucciones completas.
