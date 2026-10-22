package com.cine.benja.cine_core.messaging;

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

/** Consumer de auditoría con ACK manual y reintento único antes de enviar a DLQ. */
@Component
public class AuditoriaConsumer {
    private static final Logger log = LoggerFactory.getLogger(AuditoriaConsumer.class);
    private final ObjectMapper objectMapper;

    public AuditoriaConsumer(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    @RabbitListener(queues = "${app.rabbitmq.queues.audit}", containerFactory = "manualAckListenerContainerFactory")
    public void consumir(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            String raw = new String(message.getBody(), StandardCharsets.UTF_8);
            JsonNode json = objectMapper.readTree(raw);
            if (json == null || !json.hasNonNull("event") || !json.hasNonNull("reservationId")) {
                throw new IllegalArgumentException("Evento de auditoría incompleto");
            }
            log.info("AUDITORÍA {} reserva={} email={}", json.path("event").asText(),
                json.path("reservationId").asText(), json.path("email").asText());
            channel.basicAck(tag, false);
        } catch (Exception ex) {
            boolean retry = !Boolean.TRUE.equals(message.getMessageProperties().getRedelivered());
            log.error("Error de auditoría redelivered={}, requeue={}; el mensaje irá a DLQ tras el reintento: {}",
                message.getMessageProperties().getRedelivered(), retry,
                new String(message.getBody(), StandardCharsets.UTF_8), ex);
            channel.basicNack(tag, false, retry);
        }
    }
}
