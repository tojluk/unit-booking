package com.spribe.booking.controller;

import com.spribe.booking.dto.PaymentResponse;
import com.spribe.booking.model.Booking;
import com.spribe.booking.model.Payment;
import com.spribe.booking.model.Unit;
import com.spribe.booking.model.types.BookingStatus;
import com.spribe.booking.model.types.PaymentStatus;
import com.spribe.booking.repository.BookingRepository;
import com.spribe.booking.repository.PaymentRepository;
import com.spribe.booking.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static com.spribe.booking.testfixture.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * PaymentControllerIntegrationTest class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UnitRepository unitRepository;

    private Long unitId;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll().block();
        bookingRepository.deleteAll().block();
        unitRepository.deleteAll().block();
        unitId = unitRepository.save(createTestUnit().build()).block().getId();
    }

    @Test
    void shouldProcessPaymentSuccessfully_whenProcessPayment_givenExistingPendingPayment() {
        // given
        Booking booking = createExpectedBooking(BookingStatus.PENDING, unitId);
        Booking savedBooking = bookingRepository.save(booking).block();

        Payment pendingPayment = createExpectedPayment(savedBooking.getId());
        pendingPayment.setStatus(PaymentStatus.PENDING);
        Payment savedPayment = paymentRepository.save(pendingPayment).block();

        // when
        PaymentResponse response = webTestClient.post()
                                                .uri("/api/v1/payments/{paymentId}/process", savedPayment.getId())
                                                .exchange()
                                                .expectStatus().isOk()
                                                .expectBody(PaymentResponse.class)
                                                .returnResult()
                                                .getResponseBody();

        // then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.bookingId()).isEqualTo(savedBooking.getId());
        assertThat(response.amount()).isEqualByComparingTo(TOTAL_COST);
        assertThat(response.paymentDate()).isNotNull();

        Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).block();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(updatedPayment.getPaymentDate()).isNotNull();

        Booking updatedBooking = bookingRepository.findById(savedBooking.getId()).block();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void shouldHandleErrorProperly_whenProcessPayment_givenAlreadyCompletedPayment() {
        // given
        Booking booking = createExpectedBooking(BookingStatus.CONFIRMED, unitId);
        Booking savedBooking = bookingRepository.save(booking).block();

        Payment completedPayment = createExpectedPayment(savedBooking.getId());
        completedPayment.setStatus(PaymentStatus.COMPLETED);
        completedPayment.setPaymentDate(LocalDateTime.now().minusDays(1));
        Payment savedPayment = paymentRepository.save(completedPayment).block();

        // when-then
        webTestClient.post()
                     .uri("/api/v1/payments/{paymentId}/process", savedPayment.getId())
                     .exchange()
                     .expectStatus()
                     .is5xxServerError();
    }

    @Test
    void shouldHandleErrorProperly_whenProcessPayment_givenExpiredPayment() {
        // given
        Booking booking = createExpectedBooking(BookingStatus.PENDING, unitId);
        Booking savedBooking = bookingRepository.save(booking).block();

        Payment expiredPayment = createExpectedPayment(savedBooking.getId());
        expiredPayment.setStatus(PaymentStatus.EXPIRED);
        Payment savedPayment = paymentRepository.save(expiredPayment).block();

        // when-then
        webTestClient.post()
                     .uri("/api/v1/payments/{paymentId}/process", savedPayment.getId())
                     .exchange()
                     .expectStatus().is5xxServerError();
    }

    @Test
    void shouldUpdateUnitAvailability_whenProcessPayment_givenSuccessfulPayment() {
        // given
        Unit unit = unitRepository.findById(unitId).block();
        unit.setAvailable(false); // Unit is unavailable due to pending booking
        unitRepository.save(unit).block();

        Booking booking = createExpectedBooking(BookingStatus.PENDING, unitId);
        Booking savedBooking = bookingRepository.save(booking).block();

        Payment pendingPayment = createExpectedPayment(savedBooking.getId());
        pendingPayment.setStatus(PaymentStatus.PENDING);
        Payment savedPayment = paymentRepository.save(pendingPayment).block();

        // when
        webTestClient.post()
                     .uri("/api/v1/payments/{paymentId}/process", savedPayment.getId())
                     .exchange()
                     .expectStatus().isOk();

        // then
        Unit updatedUnit = unitRepository.findById(unitId).block();
        assertThat(updatedUnit.isAvailable()).isFalse(); // Should remain unavailable for confirmed booking
    }
}
