package com.spribe.booking.mapper;

import com.spribe.booking.dto.BookingRequest;
import com.spribe.booking.dto.BookingResponse;
import com.spribe.booking.model.Booking;
import com.spribe.booking.model.Unit;
import com.spribe.booking.model.types.BookingStatus;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

/**
 * Map booking data class
 */
@UtilityClass
public class BookingMapper {

    /**
     * Maps a BookingRequest to a Booking entity.
     *
     * @param request {@link BookingRequest} The BookingRequest object containing booking details.
     * @param unit {@link Unit}   The Unit object associated with the booking.
     * @return A Booking entity populated with the details from the request and unit.
     */
    public static Booking mapBookingRequestFromUnit(BookingRequest request, Unit unit) {
        Booking booking = new Booking();
        booking.setUnitId(unit.getId());
        booking.setUserId(request.userId());
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalCost(calculateTotalCost(unit, request));
        return booking;
    }

    /**
     * Maps a Booking entity to a BookingResponse object.
     *
     * @param booking {@link Booking} The Booking entity to be mapped.
     * @return A BookingResponse object populated with the details from the booking.
     */
    public static BookingResponse mapToBookingResponseFromBooking(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUnitId(),
                booking.getUserId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus(),
                booking.getTotalCost()
        );
    }

    private static BigDecimal calculateTotalCost(Unit unit, BookingRequest request) {
        long days = ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;
        return unit.calculateTotalCost().multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
    }

}
