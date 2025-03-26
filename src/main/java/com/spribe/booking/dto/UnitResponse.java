package com.spribe.booking.dto;

import com.spribe.booking.model.types.AccommodationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UnitResponse(
        Long id,
        Integer roomsNumber,
        AccommodationType accommodationType,
        Integer floor,
        BigDecimal baseCost,
        BigDecimal totalCost,
        Boolean isAvailable,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
