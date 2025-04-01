
package com.spribe.booking.repository;

import com.spribe.booking.model.Payment;
import com.spribe.booking.model.types.PaymentStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for managing Payment entities.
 */
@Repository
public interface PaymentRepository extends ReactiveCrudRepository<Payment, Long> {
    Flux<Payment> findByStatus(PaymentStatus status);
    Mono<Payment> findByBookingId(Long id);
    Flux<Payment> findByBookingIdAndStatus(Long id, PaymentStatus status);
}
