package com.spribe.booking.dto;

import com.spribe.booking.dto.types.SortDirection;
import com.spribe.booking.dto.types.UnitSortField;
import com.spribe.booking.model.types.AccommodationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

import static java.util.Objects.isNull;

@Schema(description = "Search request for units")
@Builder
public record UnitSearchRequest(
        @Schema(description = "User ID", example = "1")
        long userId,
        @Schema(description = "Number of rooms", example = "2")
        Integer roomsNumber,
        @Schema(description = "Type of accommodation", example = "APARTMENT")
        AccommodationType accommodationType,
        @Schema(description = "Floor number", example = "3")
        Integer floor,
        @Schema(description = "Start date of availability", example = "2024-03-01")
        LocalDate startDate,
        @Schema(description = "End date of availability", example = "2024-03-05")
        LocalDate endDate,
        @Schema(description = "Field to sort by", example = "CREATED_AT")
        UnitSortField sortBy,
        @Schema(description = "Sort direction", example = "DESC")
        SortDirection sortDirection,
        @Schema(description = "Page number", example = "1")
        int pageNo,
        @Schema(description = "Page size", example = "20")
        int pageSize
) {
    public UnitSearchRequest {
        if (pageNo < 0) pageNo = 1;
        if (pageSize <= 0) pageSize = 20;
        if (isNull(sortBy)) sortBy = UnitSortField.CREATED_AT;
        if (isNull(sortDirection)) sortDirection = SortDirection.DESC;
    }
}
