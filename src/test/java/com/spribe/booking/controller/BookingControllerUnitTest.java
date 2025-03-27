package com.spribe.booking.controller;

import com.spribe.booking.dto.BookingRequest;
import com.spribe.booking.dto.BookingResponse;
import com.spribe.booking.model.types.BookingStatus;
import com.spribe.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerUnitTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private static final LocalDate START_DATE = LocalDate.of(2024, 7, 15);
    private static final LocalDate END_DATE = LocalDate.of(2024, 7, 20);
    private static final Long UNIT_ID = 1L;
    private static final Long USER_ID = 100L;
    private static final Long BOOKING_ID = 1000L;
    private static final BigDecimal TOTAL_COST = new BigDecimal("500.00");

    private static BookingRequest createBookingRequestForValidDates() {
        return new BookingRequest(
                UNIT_ID,
                USER_ID,
                START_DATE,
                END_DATE
        );
    }

    private static BookingResponse createBookingResponseForSuccessfulBooking() {
        return new BookingResponse(
                BOOKING_ID,
                UNIT_ID,
                USER_ID,
                START_DATE,
                END_DATE,
                BookingStatus.CONFIRMED,
                TOTAL_COST
        );
    }

    private static BookingResponse createBookingResponseForCancelledBooking() {
        return new BookingResponse(
                BOOKING_ID,
                UNIT_ID,
                USER_ID,
                START_DATE,
                END_DATE,
                BookingStatus.CANCELLED,
                TOTAL_COST
        );
    }

    @Test
    void shouldReturnBookingResponse_whenCreateBooking_givenValidRequest() {
        // given
        BookingRequest givenRequest = createBookingRequestForValidDates();
        BookingResponse expected = createBookingResponseForSuccessfulBooking();

        when(bookingService.createBooking(givenRequest)).thenReturn(Mono.just(expected));

        // when
        Mono<BookingResponse> result = bookingController.createBooking(givenRequest);

        // then
        StepVerifier.create(result)
                    .assertNext(actual ->
                                        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
                    )
                    .verifyComplete();
    }

    @Test
    void shouldReturnCancelledBookingResponse_whenCancelBooking_givenValidBookingId() {
        // given
        Long givenBookingId = BOOKING_ID;
        BookingResponse expected = createBookingResponseForCancelledBooking();

        when(bookingService.cancelBooking(givenBookingId)).thenReturn(Mono.just(expected));

        // when
        Mono<BookingResponse> result = bookingController.cancelBooking(givenBookingId);

        // then
        StepVerifier.create(result)
                    .assertNext(actual ->
                                        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
                    )
                    .verifyComplete();
    }

    @Test
    void shouldPropagateError_whenCancelBooking_givenServiceReturnsError() {
        // given
        Long givenBookingId = BOOKING_ID;
        String errorMessage = "Booking not found";
        RuntimeException expected = new RuntimeException(errorMessage);

        when(bookingService.cancelBooking(givenBookingId)).thenReturn(Mono.error(expected));

        // when
        Mono<BookingResponse> result = bookingController.cancelBooking(givenBookingId);

        // then
        StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                                                throwable instanceof RuntimeException &&
                                                throwable.getMessage().equals(errorMessage)
                    )
                    .verify();
    }
}
