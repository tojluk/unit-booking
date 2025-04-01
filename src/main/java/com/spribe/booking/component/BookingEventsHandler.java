package com.spribe.booking.component;

import com.spribe.booking.component.event.PaymentExpirationEvent;
import com.spribe.booking.dto.BookingCancellationRequest;
import com.spribe.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
/**
 * Handles booking-related events, specifically payment expiration events.
 * This class listens for payment expiration events and triggers the cancellation of bookings.
 */
@Component
@RequiredArgsConstructor
public class BookingEventsHandler {

    private final BookingService bookingService;

    /**
     * Handles the PaymentExpirationEvent by cancelling the booking.
     *
     * @param event The PaymentExpirationEvent containing the booking ID and payment status.
     * @see com.spribe.booking.component.PaymentEventsHandler#handlePaymentDelayedEvent
     */
    @EventListener
    public void handlePaymentExpirationEvent(PaymentExpirationEvent event) {
        bookingService.cancelBooking(BookingCancellationRequest.builder()
                                             .bookingId(event.getBookingId())
                                             .paymentStatus(event.getPaymentStatus())
                                             .build())
                      .subscribe();
    }
}
