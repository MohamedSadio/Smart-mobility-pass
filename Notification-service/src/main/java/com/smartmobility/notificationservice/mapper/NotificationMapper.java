package com.smartmobility.notificationservice.mapper;

import com.smartmobility.notificationservice.dto.NotificationDto;
import com.smartmobility.notificationservice.entity.Notification;
import com.smartmobility.notificationservice.entity.NotificationType;
import com.smartmobility.notificationservice.event.NotificationEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NotificationMapper {

    private static final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal("2000");

    public Notification toEntity(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.userId());
        notification.setPassNumber(event.passNumber());
        NotificationType type = resolveType(event);
        notification.setType(type);
        notification.setMessage(buildMessage(type, event));
        return notification;
    }

    public NotificationDto.NotificationResponse toDto(Notification notification) {
        return new NotificationDto.NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getPassNumber(),
                notification.getType().name(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    private NotificationType resolveType(NotificationEvent event) {
        return switch (event.type()) {
            case "TRIP_CONFIRMED"        -> NotificationType.TRIP_CONFIRMED;
            case "INSUFFICIENT_BALANCE"  -> NotificationType.INSUFFICIENT_BALANCE;
            case "PASS_INACTIVE"         -> NotificationType.PASS_INACTIVE;
            case "PASS_SUSPENDED"        -> NotificationType.PASS_SUSPENDED;
            case "RECHARGE"              -> NotificationType.RECHARGE;
            case "DEBIT"                 -> isLowBalance(event)
                    ? NotificationType.LOW_BALANCE
                    : NotificationType.DEBIT;
            default                      -> NotificationType.DEBIT;
        };
    }

    private boolean isLowBalance(NotificationEvent event) {
        return event.balanceAfter() != null
                && event.balanceAfter().compareTo(LOW_BALANCE_THRESHOLD) < 0;
    }

    private String buildMessage(NotificationType type, NotificationEvent event) {
        return switch (type) {
            case DEBIT -> String.format(
                    "Débit de %.0f FCFA effectué sur votre pass %s. Solde restant : %.0f FCFA.",
                    event.amount(), event.passNumber(), event.balanceAfter());
            case RECHARGE -> String.format(
                    "Votre pass %s a été rechargé de %.0f FCFA. Nouveau solde : %.0f FCFA.",
                    event.passNumber(), event.amount(), event.balanceAfter());
            case LOW_BALANCE -> String.format(
                    "Solde faible sur votre pass %s : %.0f FCFA restants. Pensez à recharger.",
                    event.passNumber(), event.balanceAfter());
            case PASS_SUSPENDED -> String.format(
                    "Votre pass %s a été suspendu. Veuillez recharger pour continuer à voyager.",
                    event.passNumber());
            case TRIP_CONFIRMED -> String.format(
                    "Trajet confirmé : %s → %s (%s). Bon voyage !",
                    event.startStation(), event.endStation(), event.transportType());
            case INSUFFICIENT_BALANCE -> String.format(
                    "Trajet refusé : solde insuffisant sur votre pass %s. Veuillez recharger.",
                    event.passNumber());
            case PASS_INACTIVE -> String.format(
                    "Trajet refusé : votre pass %s est inactif. Contactez le support.",
                    event.passNumber());
        };
    }
}