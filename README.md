# Cine Nocturno

Aplicación web para consultar películas, revisar funciones y reservar asientos.

## Tecnologías utilizadas

- **React y Vite:** interfaz web.
- **Java 17 y Spring Boot:** servicios backend y lógica de negocio.
- **MySQL en Amazon RDS:** almacenamiento de funciones y reservas.
- **Firebase Authentication:** inicio de sesión con Google.
- **RabbitMQ:** procesamiento de eventos de tickets, notificaciones y auditoría, con colas de mensajes fallidos (DLQ).
- **Docker Compose:** ejecución local de RabbitMQ.
- **AWS EC2, Ubuntu y Nginx:** despliegue de la aplicación y publicación mediante HTTPS.

## Arquitectura

El frontend React se sirve mediante Nginx en EC2. Nginx también actúa como proxy HTTPS para la API. El backend se comunica con el servicio de lógica de negocio, que utiliza MySQL en RDS. RabbitMQ procesa los eventos de forma asíncrona.

## Evolución del proyecto

En la primera entrega no conseguimos dejar operativo el servidor AWS. Para esta segunda entrega se logró desplegar la aplicación en EC2, configurar HTTPS, conectar la base de datos y validar los flujos principales de autenticación y reservas.

## Pruebas realizadas

- Acceso web mediante HTTPS y redirección desde HTTP.
- Consulta del catálogo de funciones.
- Autenticación y operaciones de reserva y cancelación.
- Actualización de los asientos disponibles.
- Arranque automático de los servicios tras reiniciar EC2.
- Validación del clúster RabbitMQ y sus seis colas.
- Pruebas controladas de mensajes inválidos que fueron enviados a las tres DLQ.

Las pruebas de mensajes fallidos confirmaron el rechazo y enrutamiento hacia las DLQ. No representan una validación completa de todos los escenarios de reintento del negocio.

## Estructura del proyecto

- `cine-frontend/`: aplicación React.
- `cine-backend/`: API, autenticación y publicación de eventos.
- `cine-core/`: lógica de funciones y reservas.
- `cine-rabbit-admin/`: servicio de administración de RabbitMQ protegido por clave.
- `docker-compose.yml`: configuración de RabbitMQ para desarrollo local.
- `infra/rabbitmq/`: configuración de RabbitMQ.
- `presentacion/`: presentación de la segunda evaluación y capturas de evidencia.

## Configuración y seguridad

Los archivos `.env.example` contienen ejemplos de variables para configurar el entorno local. No se deben subir contraseñas reales, tokens, claves privadas ni archivos de credenciales de Firebase Admin.

Las credenciales de producción se configuran fuera del repositorio.
