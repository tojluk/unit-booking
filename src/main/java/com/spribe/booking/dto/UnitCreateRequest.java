package com.spribe.booking.dto;

import com.spribe.booking.model.types.AccommodationType;

import java.math.BigDecimal;

public record UnitCreateRequest(
        Integer roomsNumber,
        AccommodationType accommodationType,
        Integer floor,
        BigDecimal baseCost,
        String description
) {}
