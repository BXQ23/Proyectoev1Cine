package com.cine.benja.cine_rabbit_admin.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfig {
    @Bean public DirectExchange cineDirectExchange(@Value("${app.rabbitmq.exchanges.direct}") String name) { return new DirectExchange(name, true, false); }
    @Bean public TopicExchange cineTopicExchange(@Value("${app.rabbitmq.exchanges.topic}") String name) { return new TopicExchange(name, true, false); }
    @Bean(name="notificationsQueue") public Queue notificationsQueue(@Value("${app.rabbitmq.queues.notifications}") String name, @Value("${app.rabbitmq.dlq.notifications}") String dlq) { return QueueBuilder.durable(name).deadLetterExchange("").deadLetterRoutingKey(dlq).build(); }
    @Bean(name="ticketsQueue") public Queue ticketsQueue(@Value("${app.rabbitmq.queues.tickets}") String name, @Value("${app.rabbitmq.dlq.tickets}") String dlq) { return QueueBuilder.durable(name).deadLetterExchange("").deadLetterRoutingKey(dlq).build(); }
    @Bean(name="auditQueue") public Queue auditQueue(@Value("${app.rabbitmq.queues.audit}") String name, @Value("${app.rabbitmq.dlq.audit}") String dlq) { return QueueBuilder.durable(name).deadLetterExchange("").deadLetterRoutingKey(dlq).build(); }
    @Bean public Queue notificationsDlq(@Value("${app.rabbitmq.dlq.notifications}") String n) { return QueueBuilder.durable(n).build(); }
    @Bean public Queue ticketsDlq(@Value("${app.rabbitmq.dlq.tickets}") String n) { return QueueBuilder.durable(n).build(); }
    @Bean public Queue auditDlq(@Value("${app.rabbitmq.dlq.audit}") String n) { return QueueBuilder.durable(n).build(); }
    @Bean public Binding notificationsCreatedBinding(@org.springframework.beans.factory.annotation.Qualifier("notificationsQueue") Queue q, DirectExchange x, @Value("${app.rabbitmq.routing.reserva-created}") String key) { return BindingBuilder.bind(q).to(x).with(key); }
    @Bean public Binding notificationsCancelledBinding(@org.springframework.beans.factory.annotation.Qualifier("notificationsQueue") Queue q, DirectExchange x, @Value("${app.rabbitmq.routing.reserva-cancelled}") String key) { return BindingBuilder.bind(q).to(x).with(key); }
    @Bean public Binding ticketsCreatedBinding(@org.springframework.beans.factory.annotation.Qualifier("ticketsQueue") Queue q, DirectExchange x, @Value("${app.rabbitmq.routing.ticket-created}") String key) { return BindingBuilder.bind(q).to(x).with(key); }
    @Bean public Binding auditTopicBinding(@org.springframework.beans.factory.annotation.Qualifier("auditQueue") Queue q, TopicExchange x) { return BindingBuilder.bind(q).to(x).with("reserva.*"); }
}
