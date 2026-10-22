package com.cine.benja.cine_core.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declara toda la topologia inicial en un solo lugar usando nombres definidos en application.properties. */
@Configuration
public class RabbitTopologyConfig {
    @Bean
    public DirectExchange cineDirectExchange(@Value("${app.rabbitmq.exchanges.direct}") String name) {
        return new DirectExchange(name, true, false);
    }

    @Bean
    public TopicExchange cineTopicExchange(@Value("${app.rabbitmq.exchanges.topic}") String name) {
        return new TopicExchange(name, true, false);
    }

    @Bean(name = "notificationsQueue")
    public Queue notificationsQueue(@Value("${app.rabbitmq.queues.notifications}") String name,
                                    @Value("${app.rabbitmq.dlq.notifications}") String dlq) {
        return QueueBuilder.durable(name).deadLetterExchange("").deadLetterRoutingKey(dlq).build();
    }

    @Bean(name = "ticketsQueue")
    public Queue ticketsQueue(@Value("${app.rabbitmq.queues.tickets}") String name,
                              @Value("${app.rabbitmq.dlq.tickets}") String dlq) {
        return QueueBuilder.durable(name).deadLetterExchange("").deadLetterRoutingKey(dlq).build();
    }

    @Bean(name = "auditQueue")
    public Queue auditQueue(@Value("${app.rabbitmq.queues.audit}") String name,
                            @Value("${app.rabbitmq.dlq.audit}") String dlq) {
        return QueueBuilder.durable(name).deadLetterExchange("").deadLetterRoutingKey(dlq).build();
    }

    @Bean public Queue notificationsDlq(@Value("${app.rabbitmq.dlq.notifications}") String name) {
        return QueueBuilder.durable(name).build();
    }
    @Bean public Queue ticketsDlq(@Value("${app.rabbitmq.dlq.tickets}") String name) {
        return QueueBuilder.durable(name).build();
    }
    @Bean public Queue auditDlq(@Value("${app.rabbitmq.dlq.audit}") String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    public Binding notificationsCreatedBinding(@org.springframework.beans.factory.annotation.Qualifier("notificationsQueue") Queue queue,
            DirectExchange cineDirectExchange, @Value("${app.rabbitmq.routing.reserva-created}") String routingKey) {
        return BindingBuilder.bind(queue).to(cineDirectExchange).with(routingKey);
    }

    @Bean
    public Binding notificationsCancelledBinding(@org.springframework.beans.factory.annotation.Qualifier("notificationsQueue") Queue queue,
            DirectExchange cineDirectExchange, @Value("${app.rabbitmq.routing.reserva-cancelled}") String routingKey) {
        return BindingBuilder.bind(queue).to(cineDirectExchange).with(routingKey);
    }

    @Bean
    public Binding ticketsCreatedBinding(@org.springframework.beans.factory.annotation.Qualifier("ticketsQueue") Queue queue,
            DirectExchange cineDirectExchange, @Value("${app.rabbitmq.routing.ticket-created}") String routingKey) {
        return BindingBuilder.bind(queue).to(cineDirectExchange).with(routingKey);
    }

    @Bean
    public Binding auditTopicBinding(@org.springframework.beans.factory.annotation.Qualifier("auditQueue") Queue queue,
            TopicExchange cineTopicExchange,
            @Value("${app.rabbitmq.routing.audit-pattern}") String routingPattern) {
        return BindingBuilder.bind(queue).to(cineTopicExchange).with(routingPattern);
    }
}
