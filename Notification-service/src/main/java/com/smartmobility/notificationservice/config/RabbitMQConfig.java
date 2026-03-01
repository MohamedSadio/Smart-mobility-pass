package com.smartmobility.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME     = "mobility.events.exchange";
    public static final String ROUTING_KEY_TX    = "notification.transaction";
    public static final String ROUTING_KEY_TRIP  = "notification.trip";
    public static final String QUEUE_TRANSACTION = "notification.queue.transaction";
    public static final String QUEUE_TRIP        = "notification.queue.trip";

    @Bean
    public TopicExchange mobilityEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Queue transactionQueue() {
        return QueueBuilder.durable(QUEUE_TRANSACTION).build();
    }

    @Bean
    public Queue tripQueue() {
        return QueueBuilder.durable(QUEUE_TRIP).build();
    }

    @Bean
    public Binding transactionBinding(Queue transactionQueue, TopicExchange mobilityEventsExchange) {
        return BindingBuilder.bind(transactionQueue)
                .to(mobilityEventsExchange)
                .with(ROUTING_KEY_TX);
    }

    @Bean
    public Binding tripBinding(Queue tripQueue, TopicExchange mobilityEventsExchange) {
        return BindingBuilder.bind(tripQueue)
                .to(mobilityEventsExchange)
                .with(ROUTING_KEY_TRIP);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(converter);
        return template;
    }
}