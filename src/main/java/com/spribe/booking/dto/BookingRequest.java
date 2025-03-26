package com.spribe.booking.dto;

import java.time.LocalDate;

public record BookingRequest(
        Long unitId,
        Long userId,
        //TODO: offset
        LocalDate startDate,
        //TODO: offset
        LocalDate endDate
) {}
