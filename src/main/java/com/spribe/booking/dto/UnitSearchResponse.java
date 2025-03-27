package com.spribe.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UnitSearchResponse(
        Long id,
        Integer roomsNumber,
        String accommodationType,
        Integer floor,
        String description,
        BigDecimal baseCost,
        BigDecimal markupPercentage,
        LocalDateTime createdAt,

        Long[] usersBookingIds,
        Long[] otherBookingIds,
        Boolean isAvailableForUser,

        Long totalCount,
        Integer totalPages
) {}
