package com.spribe.booking.component;

import com.spribe.booking.component.event.BookingCancelledEvent;
import com.spribe.booking.component.event.BookingCreatedEvent;
import com.spribe.booking.component.event.PaymentExpirationEvent;
import com.spribe.booking.model.Payment;
import com.spribe.booking.model.types.PaymentStatus;
import com.spribe.booking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;
/**
 * PaymentEventsHandler is responsible for handling payment-related events.
 * It listens for booking creation and cancellation events, and manages payment expiration events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventsHandler {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * Handles the BookingCreatedEvent by scheduling a payment expiration event.
     *
     * @param event The BookingCreatedEvent containing the booking ID and expiration minutes.
     * @see com.spribe.booking.service.BookingService#createBooking
     */
    @EventListener
    public void handleBookingCreatedEvent(BookingCreatedEvent event) {
        onPaymentExpirationInitialEvent(event.getBookingId(), event.getExpirationMinutes());
    }

    /**
     * Handles the BookingCancelledEvent by cancelling the scheduled payment verification.
     *
     * @param event The BookingCancelledEvent containing the booking ID.
     * @see com.spribe.booking.service.BookingService#cancelBooking
     */
    @EventListener
    public void handleBookingCancelledEvent(BookingCancelledEvent event) {
        cancelScheduledPaymentVerification(event.getBookingId());
    }

    /**
     * Handles the PaymentDelayedEvent by processing the expired payment.
     *
     * @param bookingId The ID of the booking for which the payment has expired.
     */
    @EventListener
    public void handlePaymentDelayedEvent(Long bookingId) {
        processExpiredPayment(bookingId);
    }

    private void processExpiredPayment(Long bookingId) {
        scheduledTasks.remove(bookingId);
        paymentRepository.findByBookingIdAndStatus(bookingId, PaymentStatus.PENDING)
                         .flatMap(payment -> {
                             eventPublisher.publishEvent(PaymentExpirationEvent.builder()
                                                                 .source(this)
                                                                 .paymentStatus(PaymentStatus.EXPIRED)
                                                                 .bookingId(payment.getBookingId())
                                                                 .build());
                             return Mono.empty();
                         })
                         .subscribe();
    }

    public void onPaymentExpirationInitialEvent(Long bookingId, int minutes) {
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> eventPublisher.publishEvent(bookingId),
                new CronTrigger(getCronExpressionForNowPlusMinutes(minutes))
        );
        scheduledTasks.put(bookingId, scheduledTask);
    }

    private void cancelScheduledPaymentVerification(Long bookingId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.remove(bookingId);
        if (nonNull(scheduledTask) && !scheduledTask.isDone()) {
            scheduledTask.cancel(false);
        }
    }

    /**
     * Initializes payment events on application startup.
     * This method checks for any pending payments that have expired and publishes the corresponding events.
     *
     * @return CommandLineRunner to execute on application startup
     */
    @Bean
    @Order(1)
    public CommandLineRunner initializeEventsOnStartup() {
        return args -> {
            log.info("Initializing payments events on startup...");
            paymentRepository.findByStatus(
                                     PaymentStatus.PENDING)
                             .flatMap(payment -> {
                                 int minutesRemaining = getMinutesRemaining(payment);
                                 if (minutesRemaining > 0) {
                                     onPaymentExpirationInitialEvent(payment.getBookingId(), minutesRemaining);
                                     return Mono.empty();
                                 } else {
                                     eventPublisher.publishEvent(PaymentExpirationEvent.builder()
                                                                         .source(this)
                                                                         .paymentStatus(PaymentStatus.EXPIRED)
                                                                         .bookingId(payment.getBookingId())
                                                                         .build());
                                     return Mono.empty();
                                 }
                             })
                             .subscribe();
        };
    }

    private static int getMinutesRemaining(Payment payment) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationDate = payment.getExpirationDate();
        return Math.max(1, (int) java.time.Duration.between(now, expirationDate).toMinutes());
    }

    private static String getCronExpressionForNowPlusMinutes(int minutes) {
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(minutes);
        return String.format("%d %d %d %d %d ?",
                             futureTime.getSecond(),
                             futureTime.getMinute(),
                             futureTime.getHour(),
                             futureTime.getDayOfMonth(),
                             futureTime.getMonthValue());
    }

    /**
     * Cancels all scheduled tasks.
     * This method is used to clear all scheduled payment verification tasks.
     */
    public void cancelAllScheduledTasks() {
        scheduledTasks.forEach((bookingId, future) -> {
            if (!future.isDone()) {
                future.cancel(false);
            }
        });
        scheduledTasks.clear();
    }
}
