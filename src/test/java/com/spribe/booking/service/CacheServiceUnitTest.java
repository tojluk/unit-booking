package com.spribe.booking.service;

import com.spribe.booking.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheServiceUnitTest {

    private static final String AVAILABLE_UNITS_KEY = "available_units_count";

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ReactiveRedisTemplate<String, Long> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, Long> valueOperations;

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldReturnCachedValue_whenGetAvailableUnitsCount_givenValueExistsInCache() {
        // given
        Long expectedCount = 10L;
        when(valueOperations.get(AVAILABLE_UNITS_KEY)).thenReturn(Mono.just(expectedCount));
        when(unitRepository.countByIsAvailable(true)).thenReturn(Mono.just(0L));

        // when
        Mono<Long> result = cacheService.getAvailableUnitsCount();

        // then
        StepVerifier.create(result)
                    .expectNext(expectedCount)
                    .verifyComplete();

        verify(valueOperations).get(AVAILABLE_UNITS_KEY);
        verifyNoMoreInteractions(unitRepository);
    }

    @Test
    void shouldFetchFromDbAndUpdateCache_whenGetAvailableUnitsCount_givenValueNotInCache() {
        // given
        Long expectedCount = 5L;
        when(valueOperations.get(AVAILABLE_UNITS_KEY)).thenReturn(Mono.empty());
        when(unitRepository.countByIsAvailable(true)).thenReturn(Mono.just(expectedCount));
        when(valueOperations.set(eq(AVAILABLE_UNITS_KEY), eq(expectedCount), any(Duration.class)))
                .thenReturn(Mono.just(Boolean.TRUE));

        // when
        Mono<Long> result = cacheService.getAvailableUnitsCount();

        // then
        StepVerifier.create(result)
                    .expectNext(expectedCount)
                    .verifyComplete();

        verify(valueOperations).get(AVAILABLE_UNITS_KEY);
        verify(unitRepository).countByIsAvailable(true);
        verify(valueOperations).set(eq(AVAILABLE_UNITS_KEY), eq(expectedCount), any(Duration.class));
    }

    @Test
    void shouldIncrementCachedValue_whenIncrementAvailableUnits() {
        // given
        Long incrementedValue = 11L;
        when(valueOperations.increment(AVAILABLE_UNITS_KEY)).thenReturn(Mono.just(incrementedValue));

        // when
        Mono<Long> result = cacheService.incrementAvailableUnits();

        // then
        StepVerifier.create(result)
                    .expectNext(incrementedValue)
                    .verifyComplete();

        verify(valueOperations).increment(AVAILABLE_UNITS_KEY);
    }

    @Test
    void shouldDecrementCachedValue_whenDecrementAvailableUnits() {
        // given
        Long decrementedValue = 9L;
        when(valueOperations.decrement(AVAILABLE_UNITS_KEY)).thenReturn(Mono.just(decrementedValue));

        // when
        Mono<Long> result = cacheService.decrementAvailableUnits();

        // then
        StepVerifier.create(result)
                    .expectNext(decrementedValue)
                    .verifyComplete();

        verify(valueOperations).decrement(AVAILABLE_UNITS_KEY);
    }

    @Test
    void shouldDoNothing_whenValidateCache_givenCacheConsistent() {
        // given
        Long dbCount = 5L;
        Long cachedCount = 5L;
        when(unitRepository.countByIsAvailable(true)).thenReturn(Mono.just(dbCount));
        when(valueOperations.get(AVAILABLE_UNITS_KEY)).thenReturn(Mono.just(cachedCount));

        // when
        Mono<Void> result = cacheService.validateCache();

        // then
        StepVerifier.create(result)
                    .verifyComplete();

        verify(unitRepository).countByIsAvailable(true);
        verify(valueOperations).get(AVAILABLE_UNITS_KEY);
        verifyNoMoreInteractions(valueOperations);
    }

    @Test
    void shouldUpdateCache_whenValidateCache_givenCacheInconsistent() {
        // given
        Long dbCount = 7L;
        Long cachedCount = 5L;
        when(unitRepository.countByIsAvailable(true)).thenReturn(Mono.just(dbCount));
        when(valueOperations.get(AVAILABLE_UNITS_KEY)).thenReturn(Mono.just(cachedCount));
        when(valueOperations.set(eq(AVAILABLE_UNITS_KEY), eq(dbCount), any(Duration.class)))
                .thenReturn(Mono.just(Boolean.TRUE));

        // when
        Mono<Void> result = cacheService.validateCache();

        // then
        StepVerifier.create(result)
                    .verifyComplete();

        verify(unitRepository).countByIsAvailable(true);
        verify(valueOperations).get(AVAILABLE_UNITS_KEY);
        verify(valueOperations).set(eq(AVAILABLE_UNITS_KEY), eq(dbCount), any(Duration.class));
    }

}
