package com.spribe.booking.service;

import com.spribe.booking.dto.BookingCancellationRequest;
import com.spribe.booking.dto.BookingResponse;
import com.spribe.booking.model.Payment;
import com.spribe.booking.model.types.BookingStatus;
import com.spribe.booking.model.types.PaymentStatus;
import com.spribe.booking.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.spribe.booking.testfixture.TestFixture.createExpectedPayment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledServiceUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private ScheduledService scheduledService;

    @Captor
    private ArgumentCaptor<BookingCancellationRequest> cancellationRequestCaptor;

    @Test
    void shouldCancelBookingsForExpiredPayments_whenUpdateExpiredPaymentsAndCancelBooking_givenExpiredPaymentsExist() {
        // given
        Payment expiredPayment = createExpectedPayment()
                .status(PaymentStatus.PENDING)
                .expirationDate(LocalDateTime.now().minusHours(1))
                .build();

        when(paymentRepository.findByExpirationDateLessThanAndStatus(
                any(LocalDateTime.class), any(PaymentStatus.class)))
                .thenReturn(Flux.just(expiredPayment));

        when(bookingService.cancelBooking(any(BookingCancellationRequest.class)))
                .thenReturn(Mono.just(BookingResponse.builder()
                                              .status(BookingStatus.CANCELLED)
                                              .build()));

        // when
        scheduledService.updateExpiredPaymentsAndCancelBooking();

        // then
        verify(paymentRepository).findByExpirationDateLessThanAndStatus(
                any(LocalDateTime.class), any(PaymentStatus.class));

        verify(bookingService).cancelBooking(cancellationRequestCaptor.capture());
        BookingCancellationRequest capturedRequests = cancellationRequestCaptor.getValue();

        assertThat(capturedRequests.paymentStatus()).isEqualTo(PaymentStatus.EXPIRED);
    }

    @Test
    void shouldNotCancelAnyBookings_whenUpdateExpiredPaymentsAndCancelBooking_givenNoExpiredPaymentsExist() {
        // given
        when(paymentRepository.findByExpirationDateLessThanAndStatus(
                any(LocalDateTime.class), any(PaymentStatus.class)))
                .thenReturn(Flux.empty());

        // when
        scheduledService.updateExpiredPaymentsAndCancelBooking();

        // then
        verify(paymentRepository).findByExpirationDateLessThanAndStatus(
                any(LocalDateTime.class), any(PaymentStatus.class));
        verifyNoInteractions(bookingService);
    }
}
