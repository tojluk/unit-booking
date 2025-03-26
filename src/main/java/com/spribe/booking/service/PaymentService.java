package com.spribe.booking.service;

import com.spribe.booking.model.Booking;
import com.spribe.booking.model.Payment;
import com.spribe.booking.model.types.BookingStatus;
import com.spribe.booking.model.types.PaymentStatus;
import com.spribe.booking.repository.BookingRepository;
import com.spribe.booking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.spribe.booking.exception.ExceptionsUtils.getMonoError;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final long PAYMENT_EXPIRATION_MINUTES = 15;
    private static final String PAYMENT_IS_NOT_IN_PENDING_STATE = "Payment is not in PENDING state";

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final CacheService cacheService;
    private final RedissonClient redissonClient;

    public Mono<Payment> createPayment(Booking booking) {
        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setAmount(booking.getTotalCost());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setExpirationDate(LocalDateTime.now().plusMinutes(PAYMENT_EXPIRATION_MINUTES));
        payment.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public Mono<Payment> completePaymentAndBooking(Long paymentId) {
        String lockKey = "payment:" + paymentId;
        RLock lock = redissonClient.getLock(lockKey);
        return Mono.fromCompletionStage(lock.tryLockAsync(5, 10, TimeUnit.SECONDS))
            .filter(Boolean::booleanValue)
            .flatMap(unused ->  paymentRepository.findById(paymentId)
                                .flatMap(payment -> {
                                    if (payment.getStatus() != PaymentStatus.PENDING) {
                                        return getMonoError(PAYMENT_IS_NOT_IN_PENDING_STATE);
                                    }
                                    if (LocalDateTime.now().isAfter(payment.getExpirationDate())) {
                                        return markPaymentAsExpired(payment);
                                    }

                                    payment.setStatus(PaymentStatus.COMPLETED);
                                    payment.setPaymentDate(LocalDateTime.now());

                                    return paymentRepository.save(payment)
                                                            .flatMap(savedPayment ->
                                                                             updateBookingStatus(savedPayment.getBookingId(), BookingStatus.CONFIRMED)
                                                                                     .then(Mono.just(savedPayment)));
                                })).doFinally(signal -> lock.unlock());
    }

    public Mono<Payment> cancelPayment(Long paymentId) {
        String lockKey = "payment:" + paymentId;
        RLock lock = redissonClient.getLock(lockKey);
        return Mono.fromCompletionStage(lock.tryLockAsync(5, 10, TimeUnit.SECONDS))
                   .filter(Boolean::booleanValue)
                   .flatMap(unused ->  paymentRepository.findById(paymentId)
                                                        .flatMap(payment -> {
                                                            if (payment.getStatus() != PaymentStatus.PENDING) {
                                                                return getMonoError(PAYMENT_IS_NOT_IN_PENDING_STATE);
                                                            }
                                                            if (LocalDateTime.now().isAfter(payment.getExpirationDate())) {
                                                                return markPaymentAsExpired(payment);
                                                            }
                                                            payment.setStatus(PaymentStatus.CANCELLED);
                                                            payment.setPaymentDate(LocalDateTime.now());
                                                            return paymentRepository.save(payment);
                                                        })).doFinally(signal -> lock.unlock());
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void checkExpiredPayments() {
        String lockKey = "payment_processing_lock";
        RLock lock = redissonClient.getLock(lockKey);
        Mono.fromCompletionStage(lock.tryLockAsync(5, 10, TimeUnit.SECONDS))
            .filter(Boolean::booleanValue)
            .flatMapMany(unused -> paymentRepository.findByExpirationDateLessThanAndStatus(
                    LocalDateTime.now(),
                    PaymentStatus.PENDING
            ))
            .flatMap(this::markPaymentAsExpired)
            .doFinally(signal -> lock.unlockAsync())
            .subscribe();
    }

    private Mono<Payment> markPaymentAsExpired(Payment payment) {
        payment.setStatus(PaymentStatus.EXPIRED);
        return paymentRepository.save(payment)
                                .flatMap(savedPayment ->
                                                 updateBookingStatus(savedPayment.getBookingId(), BookingStatus.CANCELLED)
                                                         .then(cacheService.incrementAvailableUnits())
                                                         .then(Mono.just(savedPayment))
                                );
    }

    private Mono<Booking> updateBookingStatus(Long bookingId, BookingStatus status) {
        return bookingRepository.findById(bookingId)
                                .flatMap(booking -> {
                                    booking.setStatus(status);
                                    return bookingRepository.save(booking);
                                });
    }

}
