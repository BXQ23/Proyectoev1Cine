package com.cine.benja.cine_backend.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** Publica eventos luego de que cine-core confirma una operación de negocio. */
@Service
public class RabbitEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final String directExchange;
    private final String createdKey;
    private final String cancelledKey;
    private final String ticketKey;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchanges.direct}") String directExchange,
            @Value("${app.rabbitmq.routing.reserva-created}") String createdKey,
            @Value("${app.rabbitmq.routing.reserva-cancelled}") String cancelledKey,
            @Value("${app.rabbitmq.routing.ticket-created}") String ticketKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.directExchange = directExchange;
        this.createdKey = createdKey;
        this.cancelledKey = cancelledKey;
        this.ticketKey = ticketKey;
    }

    public void reservaCreada(Object reservaId, String email) {
        Map<String, Object> event = evento("RESERVA_CREADA", reservaId, email);
        publicar(createdKey, event);
        publicar(ticketKey, evento("TICKET_SOLICITADO", reservaId, email));
    }

    public void reservaCancelada(Object reservaId, String email) {
        publicar(cancelledKey, evento("RESERVA_CANCELADA", reservaId, email));
    }

    private Map<String, Object> evento(String type, Object id, String email) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("event", type);
        event.put("reservationId", id == null ? "desconocido" : id);
        event.put("email", email == null ? "desconocido" : email);
        event.put("occurredAt", Instant.now().toString());
        return event;
    }

    private void publicar(String routingKey, Map<String, Object> event) {
        try {
            rabbitTemplate.convertAndSend(directExchange, routingKey, event);
            log.info("Evento RabbitMQ publicado. routingKey={}, reserva={}", routingKey, event.get("reservationId"));
        } catch (AmqpException ex) {
            // No respondemos 500 por una reserva ya guardada. El error queda visible en los logs.
            log.error("No se pudo publicar evento RabbitMQ routingKey={}: {}", routingKey, ex.getMessage());
        }
    }
}
