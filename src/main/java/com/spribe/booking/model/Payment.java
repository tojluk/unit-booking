package com.spribe.booking.model;

import com.spribe.booking.model.types.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private LocalDateTime expirationDate;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
