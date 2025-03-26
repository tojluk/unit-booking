package com.spribe.booking.model;

import com.spribe.booking.model.types.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Represents a payment in the booking system.
 */
@Getter
@Setter
@ToString
@Table("payments")
public class Payment {
    @Id
    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private LocalDateTime expirationTime;

    @CreatedDate
    private LocalDateTime createdAt;
}
