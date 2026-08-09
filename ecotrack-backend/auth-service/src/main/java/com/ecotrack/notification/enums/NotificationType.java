package com.ecotrack.notification.enums;

/**
 * What kind of event this notification was generated from - lets the
 * frontend pick an icon/color and lets the Citizen/Recycler/Industry
 * filter their notification list if they want to.
 */
public enum NotificationType {
    PICKUP_ACCEPTED,
    PICKUP_COMPLETED,
    PICKUP_CANCELLED,
    ORDER_PLACED,
    PAYMENT_SUCCESSFUL,
    ORDER_SHIPPED,
    ORDER_DELIVERED,
    RECYCLER_APPROVED,
    INDUSTRY_APPROVED,
    REWARD_REDEEMED,
    GENERAL_ANNOUNCEMENT
}
