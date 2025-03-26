
package com.spribe.booking.repository;

import com.spribe.booking.model.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
/**
 * Repository interface for managing Event entities.
 */
@Repository
public interface EventRepository extends ReactiveCrudRepository<Event, Long> {
}
