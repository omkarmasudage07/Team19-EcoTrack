package com.ecotrack.notification.config;

import com.ecotrack.notification.constant.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Notification Service only CONSUMES events - it never publishes any
 * itself. Every event type it cares about gets its own queue bound to
 * the shared exchange with that event's routing key.
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
    public Queue pickupAcceptedQueue() {
        return new Queue(RabbitMQConstants.QUEUE_PICKUP_ACCEPTED, true);
    }

    @Bean
    public Queue pickupCompletedQueue() {
        return new Queue(RabbitMQConstants.QUEUE_PICKUP_COMPLETED, true);
    }

    @Bean
    public Queue pickupCancelledQueue() {
        return new Queue(RabbitMQConstants.QUEUE_PICKUP_CANCELLED, true);
    }

    @Bean
    public Queue orderPlacedQueue() {
        return new Queue(RabbitMQConstants.QUEUE_ORDER_PLACED, true);
    }

    @Bean
    public Queue paymentSuccessfulQueue() {
        return new Queue(RabbitMQConstants.QUEUE_PAYMENT_SUCCESSFUL, true);
    }

    @Bean
    public Queue orderShippedQueue() {
        return new Queue(RabbitMQConstants.QUEUE_ORDER_SHIPPED, true);
    }

    @Bean
    public Queue orderDeliveredQueue() {
        return new Queue(RabbitMQConstants.QUEUE_ORDER_DELIVERED, true);
    }

    @Bean
    public Queue recyclerApprovedQueue() {
        return new Queue(RabbitMQConstants.QUEUE_RECYCLER_APPROVED, true);
    }

    @Bean
    public Queue industryApprovedQueue() {
        return new Queue(RabbitMQConstants.QUEUE_INDUSTRY_APPROVED, true);
    }

    @Bean
    public Queue rewardRedeemedQueue() {
        return new Queue(RabbitMQConstants.QUEUE_REWARD_REDEEMED, true);
    }

    @Bean
    public Binding pickupAcceptedBinding(Queue pickupAcceptedQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(pickupAcceptedQueue).to(ecotrackExchange).with(RabbitMQConstants.ROUTING_KEY_PICKUP_ACCEPTED);
    }

    @Bean
    public Binding pickupCompletedBinding(Queue pickupCompletedQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(pickupCompletedQueue).to(ecotrackExchange).with(RabbitMQConstants.ROUTING_KEY_PICKUP_COMPLETED);
    }

    @Bean
    public Binding pickupCancelledBinding(Queue pickupCancelledQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(pickupCancelledQueue).to(ecotrackExchange).with(RabbitMQConstants.ROUTING_KEY_PICKUP_CANCELLED);
    }

    @Bean
    public Binding orderPlacedBinding(Queue orderPlacedQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(orderPlacedQueue).to(ecotrackExchange).with(RabbitMQConstants.ROUTING_KEY_ORDER_PLACED);
    }

    @Bean
    public Binding paymentSuccessfulBinding(Queue paymentSuccessfulQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(paymentSuccessfulQueue).to(ecotrackExchange).with(RabbitMQConstants.ROUTING_KEY_PAYMENT_SUCCESSFUL);
    }

    @Bean
    public Binding orderShippedBinding(Queue orderShippedQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(orderShippedQueue).to(ecotrackExchange).with(RabbitMQConstants.ROUTING_KEY_ORDER_SHIPPED);
    }

    @Bean
    public Binding orderDeliveredBinding(Queue orderDeliveredQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(orderDeliveredQueue).to(ecotrackExchange).with(RabbitMQConstants.ROUTING_KEY_ORDER_DELIVERED);
    }

    @Bean
    public Binding recyclerApprovedBinding(Queue recyclerApprovedQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(recyclerApprovedQueue).to(ecotrackExchange).with(RabbitMQConstants.ROUTING_KEY_RECYCLER_APPROVED);
    }

    @Bean
    public Binding industryApprovedBinding(Queue industryApprovedQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(industryApprovedQueue).to(ecotrackExchange).with(RabbitMQConstants.ROUTING_KEY_INDUSTRY_APPROVED);
    }

    @Bean
    public Binding rewardRedeemedBinding(Queue rewardRedeemedQueue, TopicExchange ecotrackExchange) {
        return BindingBuilder.bind(rewardRedeemedQueue).to(ecotrackExchange).with(RabbitMQConstants.ROUTING_KEY_REWARD_REDEEMED);
    }
}
