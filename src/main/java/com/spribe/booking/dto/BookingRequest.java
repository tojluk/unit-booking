package com.spribe.booking.dto;

import java.time.LocalDate;

public record BookingRequest(
        Long unitId,
        Long userId,
        LocalDate startDate,
        LocalDate endDate
) {}
