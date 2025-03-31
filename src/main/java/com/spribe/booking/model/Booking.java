package com.spribe.booking.model;

import com.spribe.booking.model.types.BookingStatus;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * Represents a booking in the booking system.
 * <p>
 * This class contains information about the booking, including the unit ID, user ID,
 * start and end dates, status, total cost, and timestamps for creation and last update.
 * </p>
 */
@Getter
@Setter
@ToString
@Table("bookings")
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
