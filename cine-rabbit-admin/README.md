# cine-rabbit-admin

Microservicio Spring Boot para consultar y administrar la topología RabbitMQ mediante API REST.

- Puerto local: `8082`.
- Operaciones principales: consultar topología; crear/eliminar colas, exchanges y bindings.
- Protección administrativa: header `X-Admin-Key`, configurado mediante la variable `RABBIT_ADMIN_API_KEY`.
- En AWS, este servicio queda interno; no expongas el puerto `8082` a Internet.

En Windows, ejecuta `INICIAR_RABBIT_ADMIN.bat` desde la raíz. La clave del script es únicamente para desarrollo local; usa una clave generada fuera del repositorio en cualquier entorno persistente.
