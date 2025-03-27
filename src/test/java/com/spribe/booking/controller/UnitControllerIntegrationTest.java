package com.spribe.booking.controller;

import com.spribe.booking.dto.UnitCreateRequest;
import com.spribe.booking.dto.UnitResponse;
import com.spribe.booking.model.Unit;
import com.spribe.booking.repository.BookingRepository;
import com.spribe.booking.repository.PaymentRepository;
import com.spribe.booking.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.spribe.booking.testfixture.TestFixture.createTestUnit;
import static com.spribe.booking.testfixture.TestFixture.createUnitResponse;
import static com.spribe.booking.testfixture.TestFixture.createValidUnitRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * UnitControllerIntegrationTest class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UnitControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll().block();
        bookingRepository.deleteAll().block();
        unitRepository.deleteAll().block();
    }

    @Test
    void shouldCreateUnitSuccessfully_whenCreateUnit_givenValidRequest() {
        // given
        UnitCreateRequest request = createValidUnitRequest().build();

        // when
        UnitResponse response = webTestClient.post()
                                             .uri("/api/v1/units")
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .bodyValue(request)
                                             .exchange()
                                             .expectStatus().isOk()
                                             .expectBody(UnitResponse.class)
                                             .returnResult()
                                             .getResponseBody();

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();

        UnitResponse expectedResponse = createUnitResponse(response.id()).build();
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        Unit savedUnit = unitRepository.findById(response.id()).block();
        assertThat(savedUnit).isNotNull();

        Unit expectedUnit = createTestUnit()
                .id(response.id())
                .build();

        assertThat(savedUnit)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedUnit);
    }
}
