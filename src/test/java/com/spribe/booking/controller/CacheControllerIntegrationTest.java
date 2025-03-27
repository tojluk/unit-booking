package com.spribe.booking.controller;

import com.spribe.booking.model.Unit;
import com.spribe.booking.repository.BookingRepository;
import com.spribe.booking.repository.PaymentRepository;
import com.spribe.booking.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.spribe.booking.testfixture.TestFixture.createTestUnit;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CacheControllerIntegrationTest {

    // TODO: use test fixture with predefined data; maybe use h2 + embedded-redis;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll().block();
        bookingRepository.deleteAll().block();
        unitRepository.deleteAll().block();
        redissonClient.getKeys().flushall();
    }

    @Test
    void shouldReturnCorrectCount_whenGetAvailableUnitsCount_givenMixOfAvailableAndUnavailableUnits() {
        // given
        Unit unit1 = createTestUnit().isAvailable(true).build();
        Unit unit2 = createTestUnit().isAvailable(false).build();

        Flux<Unit> savedUnits = Flux.just(unit1, unit2)
                                    .flatMap(unitRepository::save);

        StepVerifier.create(savedUnits.collectList())
                    .expectNextCount(1)
                    .verifyComplete();

        // when
        Long count = webTestClient.get()
                                  .uri("/api/v1/cache")
                                  .exchange()
                                  .expectStatus().isOk()
                                  .expectBody(Long.class)
                                  .returnResult()
                                  .getResponseBody();

        // then
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void shouldReflectCorrectCount_whenGetAvailableUnitsCount_afterCacheEviction() {
        // given
        Unit unit1 = createTestUnit().isAvailable(true).build();
        unitRepository.save(unit1).block();

        Long initialCount = webTestClient.get()
                                         .uri("/api/v1/cache")
                                         .exchange()
                                         .expectStatus().isOk()
                                         .expectBody(Long.class)
                                         .returnResult()
                                         .getResponseBody();

        assertThat(initialCount).isEqualTo(1L);

        // when - add a new unit and evict cache
        Unit unit2 = createTestUnit().isAvailable(true).build();
        unitRepository.save(unit2).block();
        redissonClient.getKeys().flushall();

        // then
        Long updatedCount = webTestClient.get()
                                         .uri("/api/v1/cache")
                                         .exchange()
                                         .expectStatus().isOk()
                                         .expectBody(Long.class)
                                         .returnResult()
                                         .getResponseBody();

        assertThat(updatedCount).isEqualTo(2L);
    }
}
