package com.spribe.booking.service;

import com.spribe.booking.model.Booking;
import com.spribe.booking.model.Payment;
import com.spribe.booking.model.types.PaymentStatus;
import com.spribe.booking.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static com.spribe.booking.testfixture.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    private Payment pendingPayment;
    private Payment completedPayment;
    private Payment expiredPayment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = createExpectedBooking(UNIT_ID).build();

        pendingPayment = createExpectedPayment()
                .bookingId(booking.getId())
                .build();
        pendingPayment.setStatus(PaymentStatus.PENDING);

        completedPayment = createExpectedPayment()
                .bookingId(booking.getId())
                .build();
        completedPayment.setStatus(PaymentStatus.COMPLETED);
        completedPayment.setPaymentDate(LocalDateTime.now().minusDays(1));

        expiredPayment = createExpectedPayment()
                .bookingId(booking.getId())
                .build();
        expiredPayment.setStatus(PaymentStatus.EXPIRED);
    }

    @Test
    void shouldCreatePaymentSuccessfully_whenCreatePayment_givenValidBooking() {
        // given
        when(paymentRepository.save(any(Payment.class))).thenReturn(Mono.just(pendingPayment));

        // when
        Mono<Payment> result = paymentService.createPayment(booking);

        // then
        StepVerifier.create(result)
                    .expectNextMatches(payment -> {
                        return payment.equals(pendingPayment);
                    })
                    .verifyComplete();

        verify(paymentRepository).save(paymentCaptor.capture());
        Payment capturedPayment = paymentCaptor.getValue();
        assertThat(capturedPayment.getBookingId()).isEqualTo(booking.getId());
        assertThat(capturedPayment.getAmount()).isEqualTo(TOTAL_COST);
        assertThat(capturedPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void shouldUpdatePaymentSuccessfully_whenUpdatePayment_givenPendingPayment() {
        // given
        Payment updatedPayment = Payment.builder()
                .id(pendingPayment.getId())
                .bookingId(pendingPayment.getBookingId())
                .amount(pendingPayment.getAmount())
                .status(PaymentStatus.COMPLETED)
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.findById(pendingPayment.getId())).thenReturn(Mono.just(pendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(Mono.just(updatedPayment));

        // when
        Mono<Payment> result = paymentService.updatePaymentByBookingId(pendingPayment.getBookingId(), PaymentStatus.COMPLETED);

        // then
        StepVerifier.create(result)
                    .expectNextMatches(payment -> PaymentStatus.COMPLETED.equals(payment.getStatus())
                                                  && payment.getPaymentDate() != null)
                    .verifyComplete();

        verify(paymentRepository).save(paymentCaptor.capture());
        Payment capturedPayment = paymentCaptor.getValue();
        assertThat(capturedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(capturedPayment.getPaymentDate()).isNotNull();
    }

    @Test
    void shouldReturnError_whenUpdatePayment_givenCompletedPayment() {
        // given
        when(paymentRepository.findById(completedPayment.getId()))
                .thenReturn(Mono.just(completedPayment));

        // when
        Mono<Payment> result = paymentService.updatePaymentByBookingId(completedPayment.getBookingId(), PaymentStatus.CANCELED);

        // then
        StepVerifier.create(result)
                    .expectErrorMessage("Payment is not in PENDING state")
                    .verify();
    }

    @Test
    void shouldReturnError_whenUpdatePayment_givenExpiredPayment() {
        // given
        when(paymentRepository.findById(expiredPayment.getId()))
                .thenReturn(Mono.just(expiredPayment));

        // when
        Mono<Payment> result = paymentService.updatePaymentByBookingId(expiredPayment.getBookingId(), PaymentStatus.COMPLETED);

        // then
        StepVerifier.create(result)
                    .expectErrorMessage("Payment is not in PENDING state")
                    .verify();
    }
}
