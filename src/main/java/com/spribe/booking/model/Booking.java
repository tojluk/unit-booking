package com.spribe.booking.model;

import com.spribe.booking.model.types.BookingStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Table("bookings")
public class Booking {
    @Id
    private Long id;
    private Long unitId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
    private BigDecimal totalCost;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
