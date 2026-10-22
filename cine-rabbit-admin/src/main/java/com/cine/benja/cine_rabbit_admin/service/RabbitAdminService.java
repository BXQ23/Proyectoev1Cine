package com.cine.benja.cine_rabbit_admin.service;

import com.cine.benja.cine_rabbit_admin.dto.BindingRequest;
import com.cine.benja.cine_rabbit_admin.dto.ExchangeRequest;
import com.cine.benja.cine_rabbit_admin.dto.QueueRequest;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {
    private final RabbitAdmin rabbitAdmin;

    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    public void createQueue(QueueRequest request) {
        boolean durable = request.durable() == null || request.durable();
        Queue queue;
        if (request.deadLetterQueue() != null && !request.deadLetterQueue().isBlank()) {
            queue = QueueBuilder.durable(request.name()).deadLetterExchange("")
                .deadLetterRoutingKey(request.deadLetterQueue()).build();
        } else {
            queue = new Queue(request.name(), durable, false, false);
        }
        rabbitAdmin.declareQueue(queue);
    }

    public void deleteQueue(String name) {
        validarNombre(name, "cola");
        rabbitAdmin.deleteQueue(name);
    }

    public void createExchange(ExchangeRequest request) {
        boolean durable = request.durable() == null || request.durable();
        AbstractExchange exchange = switch (request.type()) {
            case "direct" -> new DirectExchange(request.name(), durable, false);
            case "topic" -> new TopicExchange(request.name(), durable, false);
            case "fanout" -> new FanoutExchange(request.name(), durable, false);
            default -> throw new IllegalArgumentException("Tipo de exchange no permitido");
        };
        rabbitAdmin.declareExchange(exchange);
    }

    public void deleteExchange(String name) {
        validarNombre(name, "exchange");
        rabbitAdmin.deleteExchange(name);
    }

    public void createBinding(BindingRequest request) {
        validarNombre(request.queue(), "cola");
        validarNombre(request.exchange(), "exchange");
        if (rabbitAdmin.getQueueInfo(request.queue()) == null) {
            throw new IllegalArgumentException("No existe la cola: " + request.queue());
        }
        Binding binding = new Binding(request.queue(), Binding.DestinationType.QUEUE,
            request.exchange(), request.routingKey(), null);
        rabbitAdmin.declareBinding(binding);
    }

    public void deleteBinding(BindingRequest request) {
        validarNombre(request.queue(), "cola");
        validarNombre(request.exchange(), "exchange");
        Binding binding = new Binding(request.queue(), Binding.DestinationType.QUEUE,
            request.exchange(), request.routingKey(), null);
        rabbitAdmin.removeBinding(binding);
    }

    private void validarNombre(String name, String tipo) {
        if (name == null || !name.matches("[A-Za-z0-9][A-Za-z0-9._-]{2,127}")) {
            throw new IllegalArgumentException("Nombre de " + tipo + " inválido");
        }
    }
}
