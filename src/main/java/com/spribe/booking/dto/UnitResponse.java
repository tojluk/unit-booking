package com.spribe.booking.dto;

import com.spribe.booking.model.types.AccommodationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Unit response")
public record UnitResponse(
        @Schema(description = "Unit ID", example = "1")
        Long id,
        @Schema(description = "Number of rooms", example = "2")
        Integer roomsNumber,
        @Schema(description = "Type of accommodation", example = "APARTMENT")
        AccommodationType accommodationType,
        @Schema(description = "Floor number", example = "3")
        Integer floor,
        @Schema(description = "Base cost per night", example = "99.99")
        BigDecimal baseCost,
        @Schema(description = "Total cost including all fees", example = "149.99")
        BigDecimal totalCost,
        @Schema(description = "Availability status", example = "true")
        Boolean isAvailable,
        @Schema(description = "Unit description", example = "Spacious apartment with sea view")
        String description
) {}
