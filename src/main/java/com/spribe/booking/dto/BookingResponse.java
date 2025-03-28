package com.spribe.booking.dto;

import com.spribe.booking.model.types.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Schema(description = "Booking response")
public record BookingResponse(
        @Schema(description = "Booking ID", example = "1")
        Long id,
        @Schema(description = "Unit ID", example = "1")
        Long unitId,
        @Schema(description = "User ID", example = "1")
        Long userId,
        @Schema(description = "Start date", example = "2023-10-01")
        LocalDate startDate,
        @Schema(description = "End date", example = "2023-10-02")
        LocalDate endDate,
        @Schema(description = "Booking status", example = "CONFIRMED")
        BookingStatus status,
        @Schema(description = "Total cost", example = "99.99")
        BigDecimal totalCost
) {
}
