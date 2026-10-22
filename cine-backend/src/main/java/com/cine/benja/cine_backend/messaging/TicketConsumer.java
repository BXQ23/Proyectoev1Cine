package com.cine.benja.cine_backend.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Consumidor tickets. ACK manual; ante error reintenta una vez y luego envía el mensaje a la DLQ. */
@Component
public class TicketConsumer {
    private static final Logger log = LoggerFactory.getLogger(TicketConsumer.class);
    private final ObjectMapper objectMapper;

    public TicketConsumer(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    @RabbitListener(queues = "${app.rabbitmq.queues.tickets}", containerFactory = "manualAckListenerContainerFactory")
    public void consumir(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            String raw = new String(message.getBody(), StandardCharsets.UTF_8);
            JsonNode json = objectMapper.readTree(raw);
            if (json == null || !json.hasNonNull("event")) {
                throw new IllegalArgumentException("El mensaje debe contener la propiedad event");
            }
            log.info("Evento procesado: {}", raw);
            channel.basicAck(tag, false);
        } catch (Exception ex) {
            boolean reintentar = !Boolean.TRUE.equals(message.getMessageProperties().getRedelivered());
            log.error("Error procesando mensaje en tickets. redelivered={}, requeue={}. Cuerpo={}",
                message.getMessageProperties().getRedelivered(), reintentar,
                new String(message.getBody(), StandardCharsets.UTF_8), ex);
            // Primera falla: requeue. Segunda falla: requeue=false activa la DLQ de esa cola.
            channel.basicNack(tag, false, reintentar);
        }
    }
}
