package com.spribe.booking.service;

import com.spribe.booking.dto.BookingCancellationRequest;
import com.spribe.booking.dto.BookingRequest;
import com.spribe.booking.dto.BookingResponse;
import com.spribe.booking.mapper.BookingMapper;
import com.spribe.booking.model.Booking;
import com.spribe.booking.model.Unit;
import com.spribe.booking.model.types.BookingStatus;
import com.spribe.booking.repository.BookingRepository;
import com.spribe.booking.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import static com.spribe.booking.exception.ExceptionsUtils.bookingIsBeingProcessedError;
import static com.spribe.booking.exception.ExceptionsUtils.getMonoError;
import static com.spribe.booking.mapper.BookingMapper.mapBookingRequestFromUnit;
/**
 * BookingService is a service class that handles booking-related operations.
 * It provides methods for creating and canceling bookings, as well as checking unit availability.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    public static final String BOOKING_IS_ALREADY_CANCELLED = "Booking is already cancelled";
    public static final String BOOKING_IS_ALREADY_CONFIRMED = "Booking is already confirmed";
    public static final String UNIT_IS_ALREADY_BOOKED_FOR_THESE_DATES = "Unit is already booked for these dates";

    private final BookingRepository bookingRepository;
    private final UnitRepository unitRepository;
    private final PaymentService paymentService;
    private final CacheService cacheService;
    private final RedissonClient redissonClient;
    private final UnitService unitService;
    private final TransactionalOperator transactionalOperator;

    /**
     * Creates a new booking for a unit.
     *
     * @param request {@link BookingRequest} The booking request containing the unit ID and date range.
     * @return A Mono containing the created booking response.
     */
    public Mono<BookingResponse> createBooking(BookingRequest request) {
        String lockKey = "unit:" + request.unitId();
        RLock lock = redissonClient.getLock(lockKey);
        return Mono.fromCompletionStage(lock.tryLockAsync(5, 10, TimeUnit.SECONDS))
                   .filter(Boolean::booleanValue)
                   .switchIfEmpty(Mono.defer(() ->  bookingIsBeingProcessedError(lockKey)))
                   .flatMap(unused -> processBookingCreation(request).as(transactionalOperator::transactional))
                   .flatMap(response -> cacheService.incrementAvailableUnits().thenReturn(response))
                   .doFinally(signal -> lock.unlockAsync());
    }

    private Mono<BookingResponse> processBookingCreation(BookingRequest request) {
        return checkUnitAvailability(request).flatMap(unit -> processPaymentAndUnit(request, unit));
    }

    private Mono<BookingResponse> processPaymentAndUnit(BookingRequest request, Unit unit) {
        Booking booking = mapBookingRequestFromUnit(request, unit);
        return bookingRepository.save(booking)
                                .flatMap(savedBooking -> paymentService.createPayment(savedBooking)
                                        .then(unitService.setUnitAvailability(unit.getId(), false))
                                        .thenReturn(savedBooking))
                                .map(BookingMapper::mapToBookingResponseFromBooking);
    }

    /**
     * Cancels an existing booking.
     *
     * @param request {@link BookingCancellationRequest} The ID of the booking to cancel.
     * @return A Mono containing the updated booking response.
     */
    public Mono<BookingResponse> cancelBooking(BookingCancellationRequest request) {
        String lockKey = "booking:" + request.bookingId();
        RLock lock = redissonClient.getLock(lockKey);
        return Mono.fromCompletionStage(lock.tryLockAsync(5, 10, TimeUnit.SECONDS))
                   .filter(Boolean::booleanValue)
                   .switchIfEmpty(Mono.defer(() ->  bookingIsBeingProcessedError(lockKey)))
                   .flatMap(unused -> processBookingCancellation(request)).as(transactionalOperator::transactional)
                   .flatMap(response -> cacheService.incrementAvailableUnits().thenReturn(response))
                   .doFinally(signal -> lock.unlockAsync());
    }

    private Mono<BookingResponse> processBookingCancellation(BookingCancellationRequest request) {
        return bookingRepository.findById(request.bookingId())
                                .flatMap(booking -> cancelBookingAndPayment(request, booking));
    }

    private Mono<BookingResponse> cancelBookingAndPayment(BookingCancellationRequest request, Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return getMonoError(BOOKING_IS_ALREADY_CANCELLED);
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return getMonoError(BOOKING_IS_ALREADY_CONFIRMED);
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return unitService.setUnitAvailability(booking.getUnitId(), true)
                          .then(paymentService.updatePayment(booking.getId(), request.paymentStatus()))
                          .then(bookingRepository.save(booking))
                          .map(BookingMapper::mapToBookingResponseFromBooking);
    }

    private Mono<Unit> checkUnitAvailability(BookingRequest request) {
        return unitRepository.findById(request.unitId())
                             .flatMap(unit ->findOverlappingBookings(request, unit));
    }

    private Mono<Unit> findOverlappingBookings(BookingRequest request, Unit unit) {
        return bookingRepository.findOverlappingBookings(unit.getId(),
                                                         request.startDate(),
                                                         request.endDate())
                                .hasElements()
                                .flatMap(hasOverlapping -> {
                                    if (Boolean.TRUE.equals(hasOverlapping)) {
                                        return getMonoError(UNIT_IS_ALREADY_BOOKED_FOR_THESE_DATES);
                                    }
                                    return Mono.just(unit);
                                });
    }
}
