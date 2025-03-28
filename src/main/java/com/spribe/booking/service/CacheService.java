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
/**
 * CacheService is responsible for managing the cache of available units.
 * It provides methods to get, increment, and decrement the count of available units.
 * The cache is initialized on application startup and validated periodically.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {
    private static final String AVAILABLE_UNITS_KEY = "available_units_count";

    private final UnitRepository unitRepository;
    private final ReactiveRedisTemplate<String, Long> redisTemplate;

    /**
     * Retrieves the count of available units from the cache.
     * If the count is not present in the cache, it fetches it from the database and updates the cache.
     *
     * @return A Mono containing the count of available units.
     */
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

    /**
     * Increments the count of available units in the cache.
     *
     * @return A Mono containing the new count of available units.
     */
    public Mono<Long> incrementAvailableUnits() {
        return redisTemplate.opsForValue()
                            .increment(AVAILABLE_UNITS_KEY)
                            .doOnSuccess(count ->
                                                 log.debug("Incremented available units count in cache: {}", count));
    }

    /**
     * Decrements the count of available units in the cache.
     *
     * @return A Mono containing the new count of available units.
     */
    public Mono<Long> decrementAvailableUnits() {
        return redisTemplate.opsForValue()
                            .decrement(AVAILABLE_UNITS_KEY)
                            .doOnSuccess(count ->
                                                 log.debug("Decremented available units count in cache: {}", count));
    }

    /**
     * Initializes the cache on application startup.
     * It validates the cache and updates it if necessary.
     *
     * @return A CommandLineRunner that initializes the cache.
     */
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

    /**
     * Validates the cache by comparing the cached count of available units with the actual count from the database.
     * If they differ, it updates the cache with the actual count.
     *
     * @return A Mono that completes when the validation is done.
     */
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
