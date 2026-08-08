package com.ecotrack.pickup.config;

import com.ecotrack.pickup.constant.RabbitMQConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Every microservice that publishes or listens to business events shares
 * the same topic exchange ("ecotrack.events"). Pickup Service only
 * PUBLISHES here (Pickup Accepted, Pickup Completed, Pickup Cancelled) -
 * it never consumes anything itself.
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange ecotrackExchange() {
        return new TopicExchange(RabbitMQConstants.EXCHANGE_NAME);
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
