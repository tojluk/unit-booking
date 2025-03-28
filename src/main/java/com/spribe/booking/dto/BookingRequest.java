package com.spribe.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Schema(description = "Booking request")
@Builder
public record BookingRequest(
        @Schema(description = "Unit ID", example = "1")
        Long unitId,
        @Schema(description = "User ID", example = "1")
        Long userId,
        @Schema(description = "Start date", example = "2023-10-01")
        LocalDate startDate,
        @Schema(description = "End date", example = "2023-10-02")
        LocalDate endDate
) {}
