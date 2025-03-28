package com.spribe.booking.service;

import com.spribe.booking.model.Booking;
import com.spribe.booking.model.Payment;
import com.spribe.booking.model.types.PaymentStatus;
import com.spribe.booking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.spribe.booking.exception.ExceptionsUtils.getMonoError;
import static com.spribe.booking.mapper.PaymentMapper.mapToPaymentFromBooking;

/**
 * PaymentService is responsible for managing payment-related operations.
 * It provides methods to create, complete, and cancel payments, as well as check for expired payments.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String PAYMENT_IS_NOT_IN_PENDING_STATE = "Payment is not in PENDING state";

    private final PaymentRepository paymentRepository;

    /**
     * Creates a new payment for a given booking.
     *
     * @param booking {@link Booking} The booking for which the payment is being created.
     * @return A Mono containing the created payment.
     */
    public Mono<Payment> createPayment(Booking booking) {
        Payment payment = mapToPaymentFromBooking(booking);
        return paymentRepository.save(payment);
    }

    /**
     * Update a payment
     *
     * @param paymentId {@link Long} The ID of the payment to be cancelled.
     * @param status {@link PaymentStatus} The status to set for the cancelled payment.
     * @return A Mono containing the cancelled payment.
     */
    public Mono<Payment> updatePayment(Long paymentId, PaymentStatus status) {
        return paymentRepository.findById(paymentId)
                                .flatMap(payment -> {
                                    if (payment.getStatus() != PaymentStatus.PENDING) {
                                        return getMonoError(PAYMENT_IS_NOT_IN_PENDING_STATE);
                                    }
                                    payment.setStatus(status);
                                    payment.setPaymentDate(LocalDateTime.now());
                                    return paymentRepository.save(payment);
                                });
    }

}
