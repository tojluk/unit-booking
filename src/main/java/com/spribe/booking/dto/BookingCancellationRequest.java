package com.spribe.booking.dto;

import com.spribe.booking.model.types.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Booking cancellation Request")
@Builder
public record BookingCancellationRequest (
        @Schema(description = "Booking ID", example = "1")
        Long bookingId,
        @Schema(description = "Payment status", example = "CANCELLED")
        PaymentStatus paymentStatus
) {}
