package com.spribe.booking.service;

import com.spribe.booking.dto.BookingCancellationRequest;
import com.spribe.booking.model.types.PaymentStatus;
import com.spribe.booking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ScheduledService {

    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;

    /**
     * Checks for expired payments, marks them as expired and cancel Booking
     * This method is scheduled to run every 30 seconds.
     */
    //TODO: move to kafka/db cron
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void updateExpiredPaymentsAndCancelBooking() {
        paymentRepository.findByExpirationDateLessThanAndStatus(
                                 LocalDateTime.now(),
                                 PaymentStatus.PENDING)
                         .flatMap(payment ->
                                          bookingService.cancelBooking(BookingCancellationRequest.builder()
                                                                               .paymentStatus(PaymentStatus.EXPIRED)
                                                                               .bookingId(payment.getBookingId())
                                                                               .build()))
                         .subscribe();
    }
}
