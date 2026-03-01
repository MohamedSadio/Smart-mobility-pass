package com.smartmobility.notificationservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationDto {

    public record NotificationResponse(
            UUID          id,
            UUID          userId,
            String        passNumber,
            String        type,
            String        message,
            boolean       isRead,
            LocalDateTime createdAt
    ) {}
}