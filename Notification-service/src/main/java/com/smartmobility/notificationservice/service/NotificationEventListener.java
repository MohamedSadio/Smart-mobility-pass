package com.smartmobility.notificationservice.service;

import com.smartmobility.notificationservice.config.RabbitMQConfig;
import com.smartmobility.notificationservice.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_TRANSACTION)
    public void onTransactionEvent(NotificationEvent event) {
        log.info("[LISTENER] Event billing reçu — type={}, pass={}", event.type(), event.passNumber());
        notificationService.createFromEvent(event);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_TRIP)
    public void onTripEvent(NotificationEvent event) {
        log.info("[LISTENER] Event trip reçu — type={}, pass={}", event.type(), event.passNumber());
        notificationService.createFromEvent(event);
    }
}