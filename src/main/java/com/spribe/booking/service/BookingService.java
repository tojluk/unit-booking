package com.spribe.booking.service;

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

import static com.spribe.booking.exception.ExceptionsUtils.getMonoError;
import static com.spribe.booking.mapper.BookingMapper.mapBookingRequestFromUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    public static final String BOOKING_IS_BEING_PROCESSED = "Booking is being processed";
    public static final String BOOKING_IS_ALREADY_CANCELLED = "Booking is already cancelled";
    public static final String UNIT_IS_ALREADY_BOOKED_FOR_THESE_DATES = "Unit is already booked for these dates";
    private final BookingRepository bookingRepository;
    private final UnitRepository unitRepository;
    private final PaymentService paymentService;
    private final CacheService cacheService;
    private final RedissonClient redissonClient;
    private final UnitService unitService;
    private final TransactionalOperator transactionalOperator;

    public Mono<BookingResponse> createBooking(BookingRequest request) {
        String lockKey = "unit:" + request.unitId();
        RLock lock = redissonClient.getLock(lockKey);
        return Mono.fromCompletionStage(lock.tryLockAsync(5, 10, TimeUnit.SECONDS))
                   .filter(Boolean::booleanValue)
                   .switchIfEmpty(getMonoError(BOOKING_IS_BEING_PROCESSED))
                   .flatMap(unused -> checkUnitAvailability(request)
                           .flatMap(unit -> {
                               Booking booking = mapBookingRequestFromUnit(request, unit);

                               return bookingRepository.save(booking)
                                                       .flatMap(savedBooking -> paymentService.createPayment(savedBooking)
                                                                                              .then(unitService.setUnitAvailability(unit.getId(), false))
                                                                                              .then(cacheService.decrementAvailableUnits())
                                                                                              .thenReturn(savedBooking))
                                                       .map(BookingMapper::mapToBookingResponseFromBooking);
                           })
                   )
                   .as(transactionalOperator::transactional)
                   .doFinally(signal -> lock.unlockAsync());
    }

    public Mono<BookingResponse> cancelBooking(Long bookingId) {
        String lockKey = "booking:" + bookingId;
        RLock lock = redissonClient.getLock(lockKey);
        return Mono.fromCompletionStage(lock.tryLockAsync(5, 10, TimeUnit.SECONDS))
                   .filter(Boolean::booleanValue)
                   .switchIfEmpty(getMonoError(BOOKING_IS_BEING_PROCESSED))
                   .flatMap(unused -> bookingRepository.findById(bookingId)
                                                       .flatMap(booking -> {
                                                           if (booking.getStatus() == BookingStatus.CANCELLED) {
                                                               return getMonoError(BOOKING_IS_ALREADY_CANCELLED);
                                                           }
                                                           booking.setStatus(BookingStatus.CANCELLED);
                                                           return unitService.setUnitAvailability(booking.getUnitId(), true)
                                                                      .then(paymentService.cancelPayment(booking.getId()))
                                                                      .then(cacheService.incrementAvailableUnits())
                                                                      .then(bookingRepository.save(booking))
                                                                      .map(BookingMapper::mapToBookingResponseFromBooking);
                                                       }))
                   .as(transactionalOperator::transactional)
                   .doFinally(signal -> lock.unlock());
    }

    private Mono<Unit> checkUnitAvailability(BookingRequest request) {
        return unitRepository.findById(request.unitId())
                             .flatMap(unit ->
                                              bookingRepository.findOverlappingBookings(
                                                                       unit.getId(),
                                                                       request.startDate(),
                                                                       request.endDate()
                                                               )
                                                               .hasElements()
                                                               .flatMap(hasOverlapping -> {
                                                                   if (Boolean.TRUE.equals(hasOverlapping)) {
                                                                       return getMonoError(UNIT_IS_ALREADY_BOOKED_FOR_THESE_DATES);
                                                                   }
                                                                   return Mono.just(unit);
                                                               })
                             );
    }
}
