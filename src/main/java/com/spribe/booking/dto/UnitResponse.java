package com.spribe.booking.dto;

import com.spribe.booking.model.types.AccommodationType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UnitResponse(
        Long id,
        Integer roomsNumber,
        AccommodationType accommodationType,
        Integer floor,
        BigDecimal baseCost,
        BigDecimal totalCost,
        Boolean isAvailable,
        String description
) {}
