package com.spribe.booking.repository;

import com.spribe.booking.model.Booking;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface BookingRepository extends ReactiveCrudRepository<Booking, Long> {

    @Query("""
                SELECT * FROM (
                    SELECT * FROM bookings
                    WHERE unit_id = :unitId
                    AND status IN ('PENDING', 'CONFIRMED')
                    AND start_date <= :endDate
                    AND end_date >= :startDate
                    UNION
                    SELECT * FROM bookings
                    WHERE unit_id = :unitId
                    AND status IN ('PENDING', 'CONFIRMED')
                    AND start_date <= :startDate
                    AND end_date >= :startDate
                    UNION
                    SELECT * FROM bookings
                    WHERE unit_id = :unitId
                    AND status IN ('PENDING', 'CONFIRMED')
                    AND start_date >= :startDate
                    AND end_date <= :endDate
                ) AS overlapping_bookings
            """)
    Flux<Booking> findOverlappingBookings(
            Long unitId,
            LocalDate startDate,
            LocalDate endDate
    );
}
