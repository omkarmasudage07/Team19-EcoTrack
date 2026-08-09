package com.ecotrack.material.config;

import com.ecotrack.material.constant.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Material Service both PUBLISHES (order.placed, payment.successful,
 * order.shipped, order.delivered, material.listed, ecopoints.awarded)
 * and CONSUMES (pickup.completed) events on the same shared exchange.
 *
 * The queue declared here is exclusive to this service - each
 * microservice that wants to react to "pickup.completed" declares its
 * own queue bound to the same routing key, so every interested service
 * gets its own copy of the event (standard topic-exchange fan-out).
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange ecotrackExchange() {
        return new TopicExchange(RabbitMQConstants.EXCHANGE_NAME);
    }

    @Bean
    public Queue pickupCompletedQueue() {
        return new Queue(RabbitMQConstants.QUEUE_MATERIAL_PICKUP_COMPLETED, true);
    }

    @Bean
    public Binding pickupCompletedBinding(Queue pickupCompletedQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(pickupCompletedQueue)
                .to(ecotrackExchange)
                .with(RabbitMQConstants.ROUTING_KEY_PICKUP_COMPLETED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
