package com.spribe.booking.model.types;
/**
 * Enum representing the status of a payment.
 * <p>
 * This enum is used to indicate the current status of a payment in the booking system.
 * </p>
 */
public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED,
    EXPIRED
}
