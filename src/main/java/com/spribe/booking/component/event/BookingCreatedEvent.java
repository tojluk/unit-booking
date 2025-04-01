package com.spribe.booking.component.event;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BookingCreatedEvent extends ApplicationEvent {
    private final Long bookingId;
    private final int expirationMinutes;

    @Builder
    public BookingCreatedEvent(Object source, Long bookingId, int expirationMinutes) {
        super(source);
        this.bookingId = bookingId;
        this.expirationMinutes = expirationMinutes;
    }
}
