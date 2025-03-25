package com.spribe.booking.repository;

import com.spribe.booking.model.Booking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface BookingRepository extends ReactiveCrudRepository<Booking, Long> {
    Flux<Booking> findByUnitIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            Long unitId,
            LocalDate startDate,
            LocalDate endDate
    );

    Flux<Booking> findByUserId(Long userId);
}
