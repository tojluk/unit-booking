package com.spribe.booking.testfixture;

import com.spribe.booking.dto.BookingCancellationRequest;
import com.spribe.booking.dto.BookingRequest;
import com.spribe.booking.dto.BookingResponse;
import com.spribe.booking.dto.UnitCreateRequest;
import com.spribe.booking.dto.UnitResponse;
import com.spribe.booking.dto.UnitSearchRequest;
import com.spribe.booking.dto.UnitSearchResponse;
import com.spribe.booking.dto.types.SortDirection;
import com.spribe.booking.dto.types.UnitSortField;
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
    public static final Long BOOKING_ID = 1000L;
    public static final BigDecimal TTL_BOOKING_PRICE = new BigDecimal("230.0000");
    public static final Long UNIT_ID = 1L;

    public static Unit.UnitBuilder createTestUnit() {
        return Unit.builder()
                .baseCost(BASE_COST)
                .markupPercentage(PERCENTAGE)
                .accommodationType(AccommodationType.APARTMENTS)
                .roomsNumber(2)
                .floor(1)
                .isAvailable(true);
    }

    public static BookingRequest.BookingRequestBuilder createBookingRequestForValidDates() {
        return BookingRequest.builder()
                .unitId(UNIT_ID)
                .userId(USER_ID)
                .startDate(START_DATE)
                .endDate(END_DATE);
    }

    public static BookingResponse.BookingResponseBuilder createBookingResponsePending() {
        return BookingResponse.builder()
                .id(BOOKING_ID)
                .unitId(UNIT_ID)
                .userId(USER_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .status(BookingStatus.PENDING)
                .totalCost(TTL_BOOKING_PRICE);
    }

    public static BookingCancellationRequest.BookingCancellationRequestBuilder createBookingCancellationRequest() {
        return BookingCancellationRequest.builder()
                .bookingId(BOOKING_ID)
                .paymentStatus(PaymentStatus.CANCELLED);
    }

    public static Booking.BookingBuilder createExpectedBooking(Long unitId) {
        return Booking.builder()
                .unitId(unitId)
                .userId(USER_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .status(BookingStatus.PENDING)
                .totalCost(TOTAL_COST);
    }

    public static Booking.BookingBuilder createOverlappingBooking() {
        return Booking.builder()
                .unitId(UNIT_ID)
                .userId(USER_ID)
                .startDate(START_DATE.minusDays(2))
                .endDate(START_DATE.plusDays(2))
                .status(BookingStatus.CONFIRMED)
                .totalCost(OVERLAPPING_TOTAL_COST);
    }

    public static Payment.PaymentBuilder createExpectedPayment() {
        return Payment.builder()
                .bookingId(BOOKING_ID)
                .amount(TOTAL_COST)
                .status(PaymentStatus.PENDING)
                .expirationDate(LocalDateTime.now().plusMinutes(15));
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

    public static UnitSearchRequest.UnitSearchRequestBuilder createUnitSearchRequest() {
        return UnitSearchRequest.builder()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .userId(USER_ID)
                .roomsNumber(2)
                .accommodationType(AccommodationType.APARTMENTS)
                .floor(1)
                .sortBy(UnitSortField.COST)
                .sortDirection(SortDirection.ASC)
                .pageNo(0)
                .pageSize(10);
    }

    public static UnitSearchResponse.UnitSearchResponseBuilder createUnitSearchResponse(Long id) {
        return UnitSearchResponse.builder()
                .id(id)
                .description("Description for unit " + id)
                .roomsNumber(2)
                .floor(1)
                .totalCount(1L)
                .totalPages(1L)
                .markupPercentage(PERCENTAGE)
                .accommodationType(AccommodationType.APARTMENTS.name())
                .baseCost(BASE_COST)
                .isAvailableForUser(true);
    }
}
