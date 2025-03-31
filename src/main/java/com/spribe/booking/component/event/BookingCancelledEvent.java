package com.spribe.booking.component.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BookingCancelledEvent extends ApplicationEvent {
    private final Long bookingId;

    public BookingCancelledEvent(Object source, Long bookingId) {
        super(source);
        this.bookingId = bookingId;
    }
}
