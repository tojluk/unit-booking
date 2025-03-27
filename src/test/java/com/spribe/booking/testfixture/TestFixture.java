package com.spribe.booking.testfixture;

import com.spribe.booking.dto.BookingRequest;
import com.spribe.booking.dto.UnitCreateRequest;
import com.spribe.booking.dto.UnitResponse;
import com.spribe.booking.model.Booking;
import com.spribe.booking.model.Payment;
import com.spribe.booking.model.Unit;
import com.spribe.booking.model.types.AccommodationType;
import com.spribe.booking.model.types.BookingStatus;
import com.spribe.booking.model.types.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestFixture {

    public static final BigDecimal BASE_COST = new BigDecimal("100.00");
    public static final BigDecimal PERCENTAGE = new BigDecimal("15.00");
    public static final BigDecimal TTL_UNIT_COST = new BigDecimal("115.0000");
    public static final LocalDate START_DATE = LocalDate.of(2024, 7, 15);
    public static final LocalDate END_DATE = LocalDate.of(2024, 7, 20);
    public static final Long USER_ID = 100L;
    public static final BigDecimal TOTAL_COST = new BigDecimal("690.00");
    public static final BigDecimal OVERLAPPING_TOTAL_COST = new BigDecimal("200.00");

    public static Unit.UnitBuilder createTestUnit() {
        return Unit.builder()
                .baseCost(BASE_COST)
                .markupPercentage(PERCENTAGE)
                .accommodationType(AccommodationType.APARTMENTS)
                .roomsNumber(2)
                .floor(1)
                .isAvailable(true);
    }

    public static BookingRequest createBookingRequestForValidDates(Long unitId) {
        return new BookingRequest(
                unitId,
                USER_ID,
                START_DATE,
                END_DATE
        );
    }

    public static Booking createExpectedBooking(BookingStatus status, Long unitId) {
        Booking booking = new Booking();
        booking.setUnitId(unitId);
        booking.setUserId(USER_ID);
        booking.setStartDate(START_DATE);
        booking.setEndDate(END_DATE);
        booking.setStatus(status);
        booking.setTotalCost(TOTAL_COST);
        return booking;
    }

    public static Booking createOverlappingBooking(Long unitId) {
        Booking booking = new Booking();
        booking.setUnitId(unitId);
        booking.setUserId(USER_ID);
        booking.setStartDate(START_DATE.minusDays(2));
        booking.setEndDate(START_DATE.plusDays(2));
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalCost(OVERLAPPING_TOTAL_COST);
        return booking;
    }

    public static Payment createExpectedPayment(Long bookingId) {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(TOTAL_COST);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setExpirationDate(LocalDateTime.now().plusMinutes(15));
        return payment;
    }

    public static UnitCreateRequest.UnitCreateRequestBuilder createValidUnitRequest() {
        return UnitCreateRequest.builder()
                .accommodationType(AccommodationType.APARTMENTS)
                .roomsNumber(2)
                .baseCost(BASE_COST)
                .floor(1);
    }

    public static UnitResponse.UnitResponseBuilder createUnitResponse(Long id) {
        return UnitResponse.builder()
                .id(id)
                .accommodationType(AccommodationType.APARTMENTS)
                .roomsNumber(2)
                .floor(1)
                .baseCost(BASE_COST)
                .totalCost(TTL_UNIT_COST)
                .isAvailable(true);
    }
}
