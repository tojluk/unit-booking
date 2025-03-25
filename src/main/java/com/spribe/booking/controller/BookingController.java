package com.spribe.booking.controller;

import com.spribe.booking.model.Booking;
import com.spribe.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    @GetMapping("/{id}")
    public Mono<Booking> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public Flux<Booking> findAll() {
        return service.findAll();
    }
}
