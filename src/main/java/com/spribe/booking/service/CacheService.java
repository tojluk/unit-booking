package com.spribe.booking.service;

import com.spribe.booking.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {
    private static final String AVAILABLE_UNITS_KEY = "available_units_count";

    private final UnitRepository unitRepository;
    private final ReactiveRedisTemplate<String, Long> redisTemplate;

    public Mono<Long> getAvailableUnitsCount() {
        return redisTemplate.opsForValue()
                            .get(AVAILABLE_UNITS_KEY)
                            .switchIfEmpty(
                                    unitRepository.countByIsAvailable(true)
                                                  .flatMap(count ->
                                                                   redisTemplate.opsForValue()
                                                                                .set(AVAILABLE_UNITS_KEY,
                                                                                     count,
                                                                                     Duration.ofHours(1))
                                                                                .thenReturn(count))
                            )
                            .doOnSuccess(count ->
                                                 log.debug("Retrieved available units count from cache: {}", count));
    }

    public Mono<Long> incrementAvailableUnits() {
        return redisTemplate.opsForValue()
                            .increment(AVAILABLE_UNITS_KEY)
                            .doOnSuccess(count ->
                                                 log.debug("Incremented available units count in cache: {}", count));
    }

    public Mono<Long> decrementAvailableUnits() {
        return redisTemplate.opsForValue()
                            .decrement(AVAILABLE_UNITS_KEY)
                            .doOnSuccess(count ->
                                                 log.debug("Decremented available units count in cache: {}", count));
    }

    @Bean
    @Order(1)
    public CommandLineRunner initializeCacheOnStartup() {
        return args -> {
            log.info("Initializing cache on startup...");
            validateCache()
                    .doOnSuccess(v -> log.info("Cache initialized successfully"))
                    .doOnError(e -> log.error("Failed to initialize cache", e))
                    .block(Duration.ofSeconds(10));
        };
    }

    public Mono<Void> validateCache() {
        return Mono.zip(unitRepository.countByIsAvailable(true),
                        redisTemplate.opsForValue()
                                     .get(AVAILABLE_UNITS_KEY)
                                     .defaultIfEmpty(0L))
                   .filter(tuple -> !tuple.getT1().equals(tuple.getT2()))
                   .flatMap(tuple -> {
                       Long actualCount = tuple.getT1();
                       Long cachedCount = tuple.getT2();
                       log.warn("Cache inconsistency detected. Cached: {}, Actual: {}",
                                cachedCount, actualCount);
                       return redisTemplate.opsForValue()
                                           .set(AVAILABLE_UNITS_KEY, actualCount, Duration.ofHours(1));
                   }).then();
    }
}
