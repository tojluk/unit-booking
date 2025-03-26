package com.spribe.booking.exception;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

/**
 * Exceptions Utils class
 */
@UtilityClass
public class ExceptionsUtils {

    public static <T> Mono<T> getMonoError(String message) {
        return Mono.error(new IllegalStateException(message));
    }
}
