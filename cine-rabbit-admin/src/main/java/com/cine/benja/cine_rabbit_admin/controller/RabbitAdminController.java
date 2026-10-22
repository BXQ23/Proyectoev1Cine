package com.cine.benja.cine_rabbit_admin.controller;

import com.cine.benja.cine_rabbit_admin.dto.BindingRequest;
import com.cine.benja.cine_rabbit_admin.dto.ExchangeRequest;
import com.cine.benja.cine_rabbit_admin.dto.QueueRequest;
import com.cine.benja.cine_rabbit_admin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class RabbitAdminController {
    private final RabbitAdminService service;
    private final Map<String, Object> topology;

    public RabbitAdminController(RabbitAdminService service,
            @Value("${app.rabbitmq.exchanges.direct}") String directExchange,
            @Value("${app.rabbitmq.exchanges.topic}") String topicExchange,
            @Value("${app.rabbitmq.queues.notifications}") String notifications,
            @Value("${app.rabbitmq.queues.tickets}") String tickets,
            @Value("${app.rabbitmq.queues.audit}") String audit,
            @Value("${app.rabbitmq.dlq.notifications}") String notificationsDlq,
            @Value("${app.rabbitmq.dlq.tickets}") String ticketsDlq,
            @Value("${app.rabbitmq.dlq.audit}") String auditDlq,
            @Value("${app.rabbitmq.routing.audit-pattern}") String auditPattern) {
        this.service = service;
        this.topology = Map.of(
            "exchanges", Map.of("direct", directExchange, "topic", topicExchange),
            "queues", Map.of("notifications", notifications, "tickets", tickets, "audit", audit),
            "deadLetterQueues", Map.of("notifications", notificationsDlq, "tickets", ticketsDlq, "audit", auditDlq),
            "routing", Map.of("auditPattern", auditPattern)
        );
    }

    @GetMapping("/topology")
    public Map<String, Object> topology() { return topology; }

    @PostMapping("/queues")
    public ResponseEntity<Map<String, Object>> createQueue(@Valid @RequestBody QueueRequest request) {
        service.createQueue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("created", true, "queue", request.name()));
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Void> deleteQueue(@PathVariable String name) {
        service.deleteQueue(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exchanges")
    public ResponseEntity<Map<String, Object>> createExchange(@Valid @RequestBody ExchangeRequest request) {
        service.createExchange(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("created", true, "exchange", request.name(), "type", request.type()));
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Void> deleteExchange(@PathVariable String name) {
        service.deleteExchange(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bindings")
    public ResponseEntity<Map<String, Object>> createBinding(@Valid @RequestBody BindingRequest request) {
        service.createBinding(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("created", true, "queue", request.queue(), "exchange", request.exchange(), "routingKey", request.routingKey()));
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<Void> deleteBinding(@Valid @RequestBody BindingRequest request) {
        service.deleteBinding(request);
        return ResponseEntity.noContent().build();
    }
}
