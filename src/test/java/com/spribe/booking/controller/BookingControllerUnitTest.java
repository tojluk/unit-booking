package com.spribe.booking.controller;

import com.spribe.booking.dto.BookingCancellationRequest;
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

import static com.spribe.booking.testfixture.TestFixture.createBookingCancellationRequest;
import static com.spribe.booking.testfixture.TestFixture.createBookingRequestForValidDates;
import static com.spribe.booking.testfixture.TestFixture.createBookingResponsePending;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerUnitTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void shouldReturnBookingResponse_whenCreateBooking_givenValidRequest() {
        // given
        BookingRequest givenRequest = createBookingRequestForValidDates().build();
        BookingResponse expected = createBookingResponsePending().status(BookingStatus.CONFIRMED)
                .build();

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
        BookingCancellationRequest givenRequest = createBookingCancellationRequest().build();
        BookingResponse expected = createBookingResponsePending().status(BookingStatus.CANCELLED)
                .build();

        when(bookingService.cancelBooking(givenRequest)).thenReturn(Mono.just(expected));

        // when
        Mono<BookingResponse> result = bookingController.cancelBooking(givenRequest);

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
        BookingCancellationRequest givenRequest = createBookingCancellationRequest().build();
        String errorMessage = "Booking not found";
        RuntimeException expected = new RuntimeException(errorMessage);

        when(bookingService.cancelBooking(givenRequest)).thenReturn(Mono.error(expected));

        // when
        Mono<BookingResponse> result = bookingController.cancelBooking(givenRequest);

        // then
        StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                                                throwable instanceof RuntimeException &&
                                                throwable.getMessage().equals(errorMessage)
                    )
                    .verify();
    }
}
