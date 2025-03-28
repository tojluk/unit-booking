package com.spribe.booking.exception;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Utility class for handling exceptions in a reactive programming context.
 * Provides methods to create error responses for Mono types.
 */
@UtilityClass
@Slf4j
public class ExceptionsUtils {

    public static final String IS_BEING_PROCESSED = "Record is being processed";

    /**
     * Creates a Mono that emits an error with the specified message.
     *
     * @param message {@link String} The error message to be included in the exception.
     * @param <T>     The type of the Mono.
     * @return A Mono that emits an error with the specified message.
     */
    public static <T> Mono<T> getMonoError(String message) {
        return Mono.error(new IllegalStateException(message));
    }

    /**
     * Logs a warning message and creates a Mono that emits an error indicating that the booking is being processed.
     *
     * @param lockKey {@link String} The lock key associated with the booking.
     * @return A Mono that emits an error indicating that the booking is being processed.
     */
    public static Mono<Boolean> bookingIsBeingProcessedError(String lockKey) {
        log.warn(IS_BEING_PROCESSED, lockKey);
        return getMonoError(IS_BEING_PROCESSED);
    }

}
