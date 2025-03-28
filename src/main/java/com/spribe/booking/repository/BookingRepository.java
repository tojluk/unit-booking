package com.spribe.booking.repository;

import com.spribe.booking.model.Booking;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
/**
 * Repository interface for managing Booking entities.
 * <p>
 * This interface extends ReactiveCrudRepository to provide CRUD operations for Booking entities.
 * It also includes a custom query method to find overlapping bookings based on the unit ID and date range.
 * </p>
 */
@Repository
public interface BookingRepository extends ReactiveCrudRepository<Booking, Long> {
    /**
     * Finds overlapping bookings for a given unit ID and date range.
     *
     * @param unitId {@link Long}   The ID of the unit.
     * @param startDate {@link LocalDate} The start date of the booking.
     * @param endDate {@link LocalDate} The end date of the booking.
     * @return A Flux of Booking entities that overlap with the specified date range.
     */
    @Query("""
           SELECT * FROM bookings
            WHERE unit_id = :unitId
              AND status IN ('PENDING', 'CONFIRMED')
              AND start_date <= :endDate
              AND end_date >= :startDate
           """)
    Flux<Booking> findOverlappingBookings(
            Long unitId,
            LocalDate startDate,
            LocalDate endDate
    );

}
