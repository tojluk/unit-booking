package com.spribe.booking.component.event;

import com.spribe.booking.model.types.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentExpirationEvent extends ApplicationEvent {

    private final Long bookingId;
    private final PaymentStatus paymentStatus;

    @Builder
    public PaymentExpirationEvent(Object source, Long bookingId, PaymentStatus paymentStatus) {
        super(source);
        this.bookingId = bookingId;
        this.paymentStatus = paymentStatus;
    }
}
