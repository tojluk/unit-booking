package com.spribe.booking.dto;

import com.spribe.booking.model.types.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Schema(description = "Payment response")
public record PaymentResponse(
        @Schema(description = "Payment ID", example = "1")
        Long id,
        @Schema(description = "Booking ID", example = "101")
        Long bookingId,
        @Schema(description = "Payment amount", example = "199.99")
        BigDecimal amount,
        @Schema(description = "Payment status", example = "COMPLETED")
        PaymentStatus status,
        @Schema(description = "Payment date", example = "2023-10-15T14:30:00")
        LocalDateTime paymentDate,
        @Schema(description = "Payment expiration time", example = "2023-10-15T14:45:00")
        LocalDateTime expirationTime
) {}
