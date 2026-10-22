package com.cine.benja.cine_core.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** Productor del dominio: publica el evento de auditoría de la reserva. */
@Service
public class ReservaEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ReservaEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final String topicExchange;

    public ReservaEventPublisher(RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchanges.topic}") String topicExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
    }

    public void publicar(String evento, Object reservaId, String usuario) {
        Map<String, Object> mensaje = new LinkedHashMap<>();
        mensaje.put("event", evento);
        mensaje.put("reservationId", reservaId == null ? "desconocido" : reservaId);
        mensaje.put("email", usuario == null ? "desconocido" : usuario);
        mensaje.put("occurredAt", Instant.now().toString());
        try {
            rabbitTemplate.convertAndSend(topicExchange, evento.equals("RESERVA_CANCELADA") ? "reserva.cancelada" : "reserva.creada", mensaje);
            log.info("Evento de auditoría publicado: {}", evento);
        } catch (AmqpException ex) {
            log.error("No se pudo publicar auditoría para reserva {}: {}", reservaId, ex.getMessage());
        }
    }
}
