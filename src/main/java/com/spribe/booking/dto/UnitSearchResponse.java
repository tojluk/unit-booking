package com.spribe.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Search response for units")
@Builder
public record UnitSearchResponse(
        @Schema(description = "Unit ID", example = "1")
        Long id,
        @Schema(description = "Number of rooms", example = "2")
        Integer roomsNumber,
        @Schema(description = "Type of accommodation", example = "APARTMENT")
        String accommodationType,
        @Schema(description = "Floor number", example = "3")
        Integer floor,
        @Schema(description = "Unit description", example = "Spacious apartment with sea view")
        String description,
        @Schema(description = "Base cost per night", example = "99.99")
        BigDecimal baseCost,
        @Schema(description = "Markup percentage", example = "20.00")
        BigDecimal markupPercentage,
        @Schema(description = "Creation date and time", example = "2024-03-01T10:00:00")
        LocalDateTime createdAt,
        @Schema(description = "IDs of user's bookings", example = "[1, 2, 3]")
        Long[] usersBookingIds,
        @Schema(description = "IDs of other users' bookings", example = "[4, 5, 6]")
        Long[] otherBookingIds,
        @Schema(description = "Availability status for the user", example = "true")
        Boolean isAvailableForUser,
        @Schema(description = "Total count of units matching the search criteria", example = "100")
        Long totalCount,
        @Schema(description = "Total number of pages", example = "5")
        Long totalPages
) {}
