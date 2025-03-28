package com.spribe.booking.mapper;

import com.spribe.booking.model.Booking;
import com.spribe.booking.model.Payment;
import com.spribe.booking.model.types.PaymentStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class PaymentMapper {

    private static final long PAYMENT_EXPIRATION_MINUTES = 15;

    public static Payment mapToPaymentFromBooking(Booking booking) {
        return Payment.builder()
                .bookingId(booking.getId())
                .amount(booking.getTotalCost())
                .status(PaymentStatus.PENDING)
                .expirationDate(LocalDateTime.now().plusMinutes(PAYMENT_EXPIRATION_MINUTES))
                .createdAt(LocalDateTime.now())
                .build();
    }
}
