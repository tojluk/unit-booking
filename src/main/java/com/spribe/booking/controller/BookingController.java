package com.spribe.booking.controller;

import com.spribe.booking.dto.BookingCancellationRequest;
import com.spribe.booking.dto.BookingRequest;
import com.spribe.booking.dto.BookingResponse;
import com.spribe.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
/**
 * BookingController is a REST controller that handles booking-related operations.
 * It provides endpoints for creating and canceling bookings.
 */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "API controller for Bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create booking")
    public Mono<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel booking")
    public Mono<BookingResponse> cancelBooking(@RequestBody BookingCancellationRequest request) {
        return bookingService.cancelBooking(request);
    }

}
