package com.spribe.booking.service;

import com.spribe.booking.model.Booking;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookingService {


    public Mono<Booking> findById(Long id) {
        // Implementation here
        return Mono.just(new Booking());
    }

    public Flux<Booking> findAll() {
        // Implementation here
        return Flux.just(new Booking(), new Booking());
    }

    public Mono<Booking> save(Booking entity) {
        // Implementation here
        return Mono.just(entity);
    }

    public Mono<Void> deleteById(Long id) {
        // Implementation here
        return Mono.empty();
    }
}
