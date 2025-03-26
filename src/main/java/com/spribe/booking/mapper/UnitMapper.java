package com.spribe.booking.mapper;

import com.spribe.booking.dto.UnitCreateRequest;
import com.spribe.booking.dto.UnitResponse;
import com.spribe.booking.model.Unit;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class UnitMapper {

    public static Unit mapUnitFromUnitCreateRequest(UnitCreateRequest request) {
        Unit unit = new Unit();
        unit.setRoomsNumber(request.roomsNumber());
        unit.setAccommodationType(request.accommodationType());
        unit.setFloor(request.floor());
        unit.setBaseCost(request.baseCost());
        unit.setDescription(request.description());
        unit.setMarkupPercentage(BigDecimal.valueOf(15)); // TODO: Set from config
        unit.setCreatedAt(LocalDateTime.now());
        unit.setAvailable(true);
        return unit;
    }

    public UnitResponse mapUnitToUnitResponse(Unit unit) {
        return new UnitResponse(
                unit.getId(),
                unit.getRoomsNumber(),
                unit.getAccommodationType(),
                unit.getFloor(),
                unit.getBaseCost(),
                unit.calculateTotalCost(),
                //TODO: flag must be set by Booking wit rage of periods
                unit.isAvailable(),
                unit.getDescription()
        );
    }
}
