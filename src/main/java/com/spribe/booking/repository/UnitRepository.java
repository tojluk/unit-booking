package com.spribe.booking.repository;

import com.spribe.booking.model.Unit;
import com.spribe.booking.model.types.AccommodationType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

@Repository
public interface UnitRepository extends ReactiveCrudRepository<Unit, Long> {

    Flux<Unit> findByRoomsNumberAndAccommodationTypeAndFloor(
            Integer roomsNumber,
            AccommodationType type,
            Integer floor
    );

    Flux<Unit> findByBaseCostBetween(BigDecimal minCost, BigDecimal maxCost);
}
