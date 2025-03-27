package com.spribe.booking.dto;

import com.spribe.booking.dto.types.SortDirection;
import com.spribe.booking.dto.types.UnitSortField;
import com.spribe.booking.model.types.AccommodationType;

import java.time.LocalDate;

import static java.util.Objects.isNull;

public record UnitSearchRequest(
        int userId,
        Integer roomsNumber,
        AccommodationType accommodationType,
        Integer floor,
        LocalDate startDate,
        LocalDate endDate,
        UnitSortField sortBy,
        SortDirection sortDirection,
        int pageNo,
        int pageSize
) {
    public UnitSearchRequest {
        if (pageNo < 0) pageNo = 1;
        if (pageSize <= 0) pageSize = 20;
        if (isNull(sortBy)) sortBy = UnitSortField.CREATED_AT;
        if (isNull(sortDirection)) sortDirection = SortDirection.DESC;
    }
}
