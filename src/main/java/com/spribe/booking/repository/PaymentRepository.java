
package com.spribe.booking.repository;

import com.spribe.booking.model.Payment;
import com.spribe.booking.model.types.PaymentStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Repository interface for managing Payment entities.
 */
@Repository
public interface PaymentRepository extends ReactiveCrudRepository<Payment, Long> {
    Flux<Payment> findByStatus(PaymentStatus status);
    Flux<Payment> findByBookingId(Long bookingId);
    Flux<Payment> findByExpirationDateLessThanAndStatus(LocalDateTime time, PaymentStatus status);
    Mono<Payment> findFirstByBookingIdOrderByCreatedAtDesc(Long bookingId);
}
