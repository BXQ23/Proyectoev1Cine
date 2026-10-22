# Resultados de la segunda entrega

## Evolución

- **Primera entrega:** no se logró dejar un servidor AWS operativo para demostrar el sistema desplegado.
- **Segunda entrega:** se desplegó el flujo principal en EC2 con Ubuntu, Nginx y HTTPS; el backend utiliza Amazon RDS for MySQL y Firebase Authentication.

## Problemas detectados y solución aplicada

- **IP pública variable de EC2:** se utilizó un dominio DuckDNS con actualización automática para evitar depender de una IP escrita en el frontend.
- **Errores CORS/autenticación:** se ajustó la configuración CORS de Spring Security para que las respuestas de seguridad incluyan los encabezados correctos.
- **Disponibilidad de asientos en RDS:** se incorporaron triggers de inserción/eliminación para actualizar el contador al crear o cancelar reservas, junto con un índice único para impedir duplicar un asiento de la misma función.
- **Arranque tras reinicio:** los servicios Java y RabbitMQ se dejaron gestionados mediante systemd; el inicio de Spring Boot puede tardar unos segundos mientras Nginx ya está activo.
- **Exposición innecesaria de puertos:** la aplicación interna y la administración de RabbitMQ no se publican directamente a Internet; las interfaces RabbitMQ se vinculan a localhost en el entorno Compose local.

## Verificaciones efectuadas

- Página web por HTTPS y redirección HTTP → HTTPS.
- Endpoint público de funciones responde correctamente; el recurso de reservas sin token responde `401`.
- Inicio de sesión, creación de reserva, cancelación y actualización del contador de asientos verificados desde el sitio.
- Reinicio de EC2: los servicios se activan automáticamente.
- RabbitMQ: clúster de dos nodos, exchanges `cine.direct` y `cine.topic`, tres colas principales y tres DLQ.
- Enrutamiento de errores: un mensaje sintético con formato no JSON fue rechazado por cada consumidor de tickets, notificaciones y auditoría y acabó en su DLQ. Se retiraron todos los mensajes de prueba; en la comprobación final, las seis colas tenían cero mensajes listos y cero sin confirmar.

## Evidencia visual

- `../presentacion/evidencias/rabbitmq-exchanges.png`: exchanges del broker.
- `../presentacion/evidencias/rabbitmq-colas.png`: seis colas del sistema en estado `running` y sin mensajes pendientes.

## Alcance y trabajo futuro

- Las notificaciones y la generación de tickets se simulan mediante eventos y logs; no hay proveedor externo de correo/tickets.
- La prueba de DLQ cubre mensajes con formato inválido. Para una cobertura mayor, falta una prueba separada de reintentos personalizados ante un error de procesamiento de un mensaje JSON válido.
- El despliegue actual usa Nginx como proxy inverso; no se incluye Amazon API Gateway operativo en esta entrega.
