package com.spribe.booking.mapper;

import com.spribe.booking.dto.UnitCreateRequest;
import com.spribe.booking.dto.UnitResponse;
import com.spribe.booking.model.Unit;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Map unit data class
 */
@UtilityClass
public class UnitMapper {
    /**
     * Maps a UnitCreateRequest to a Unit entity.
     *
     * @param request {@link UnitCreateRequest} The UnitCreateRequest object containing unit details.
     * @return A Unit entity populated with the details from the request.
     */
    public static Unit mapUnitFromUnitCreateRequest(UnitCreateRequest request) {
        Unit unit = new Unit();
        unit.setRoomsNumber(request.roomsNumber());
        unit.setAccommodationType(request.accommodationType());
        unit.setFloor(request.floor());
        unit.setBaseCost(request.baseCost());
        unit.setDescription(request.description());
        unit.setMarkupPercentage(new BigDecimal("15.00")); // TODO: Set from config
        unit.setCreatedAt(LocalDateTime.now());
        unit.setAvailable(true);
        return unit;
    }

    /**
     * Maps a Unit entity to a UnitResponse object.
     *
     * @param unit {@link Unit} The Unit entity to be mapped.
     * @return A UnitResponse object populated with the details from the unit.
     */
    public UnitResponse mapUnitToUnitResponse(Unit unit) {
        return new UnitResponse(
                unit.getId(),
                unit.getRoomsNumber(),
                unit.getAccommodationType(),
                unit.getFloor(),
                unit.getBaseCost(),
                unit.calculateTotalCost(),
                //TODO: flag must be set by Booking with range of periods
                unit.isAvailable(),
                unit.getDescription()
        );
    }
}
