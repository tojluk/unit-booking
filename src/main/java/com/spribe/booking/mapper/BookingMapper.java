package com.spribe.booking.mapper;

import com.spribe.booking.dto.BookingRequest;
import com.spribe.booking.dto.BookingResponse;
import com.spribe.booking.model.Booking;
import com.spribe.booking.model.Unit;
import com.spribe.booking.model.types.BookingStatus;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Map booking data class
 */
@UtilityClass
public class BookingMapper {

    public static Booking mapBookingRequestFromUnit(BookingRequest request, Unit unit) {
        Booking booking = new Booking();
        booking.setUnitId(unit.getId());
        booking.setUserId(request.userId());
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalCost(calculateTotalCost(unit, request));
        booking.setCreatedAt(LocalDateTime.now());
        return booking;
    }

    public static BookingResponse mapToBookingResponseFromBooking(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUnitId(),
                booking.getUserId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus(),
                booking.getTotalCost(),
                booking.getCreatedAt()
        );
    }

    private static BigDecimal calculateTotalCost(Unit unit, BookingRequest request) {
        long days = ChronoUnit.DAYS.between(request.startDate(), request.endDate());
        return unit.calculateTotalCost().multiply(BigDecimal.valueOf(days));
    }

}
