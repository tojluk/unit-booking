package com.spribe.booking.dto;

import com.spribe.booking.model.types.AccommodationType;

import java.math.BigDecimal;

public record UnitSearchRequest(
        Integer roomsNumber,
        AccommodationType accommodationType,
        Integer floor,
        BigDecimal minCost,
        BigDecimal maxCost,
        Boolean onlyAvailable,
        String sortBy,
        String sortDirection,
        int page,
        int size
) {
    public UnitSearchRequest {
        if (onlyAvailable == null) onlyAvailable = true;
        if (page < 0) page = 1;
        if (size <= 0) size = 20;
    }
}
