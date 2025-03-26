package com.spribe.booking.dto;

import com.spribe.booking.model.types.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long bookingId,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime paymentDate,
        LocalDateTime expirationTime
) {}
