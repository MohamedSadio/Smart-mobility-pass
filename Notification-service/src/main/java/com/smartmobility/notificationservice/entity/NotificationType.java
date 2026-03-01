package com.smartmobility.notificationservice.entity;

public enum NotificationType {

    // Billing-service
    DEBIT,
    RECHARGE,
    LOW_BALANCE,
    PASS_SUSPENDED,

    // Trip-management-service
    TRIP_CONFIRMED,
    INSUFFICIENT_BALANCE,
    PASS_INACTIVE
}