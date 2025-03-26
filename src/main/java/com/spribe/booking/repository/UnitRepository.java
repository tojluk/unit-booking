package com.spribe.booking.repository;

import com.spribe.booking.model.Unit;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UnitRepository extends ReactiveCrudRepository<Unit, Long> {

    Mono<Long> countByIsAvailable(Boolean isAvailable);

}
