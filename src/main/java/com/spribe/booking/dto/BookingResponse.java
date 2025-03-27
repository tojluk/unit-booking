package com.spribe.booking.dto;

import com.spribe.booking.model.types.BookingStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BookingResponse(Long id,
        Long unitId,
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        BookingStatus status,
        BigDecimal totalCost) {
}
