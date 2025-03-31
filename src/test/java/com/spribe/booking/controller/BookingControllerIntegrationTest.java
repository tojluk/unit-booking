package com.spribe.booking.controller;

import com.spribe.booking.component.PaymentEventsHandler;
import com.spribe.booking.dto.BookingCancellationRequest;
import com.spribe.booking.dto.BookingRequest;
import com.spribe.booking.dto.BookingResponse;
import com.spribe.booking.model.Booking;
import com.spribe.booking.model.Payment;
import com.spribe.booking.model.Unit;
import com.spribe.booking.model.types.BookingStatus;
import com.spribe.booking.model.types.PaymentStatus;
import com.spribe.booking.repository.BookingRepository;
import com.spribe.booking.repository.PaymentRepository;
import com.spribe.booking.repository.UnitRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.spribe.booking.testfixture.TestFixture.END_DATE;
import static com.spribe.booking.testfixture.TestFixture.START_DATE;
import static com.spribe.booking.testfixture.TestFixture.USER_ID;
import static com.spribe.booking.testfixture.TestFixture.createBookingCancellationRequest;
import static com.spribe.booking.testfixture.TestFixture.createBookingRequestForValidDates;
import static com.spribe.booking.testfixture.TestFixture.createExpectedBooking;
import static com.spribe.booking.testfixture.TestFixture.createExpectedPayment;
import static com.spribe.booking.testfixture.TestFixture.createOverlappingBooking;
import static com.spribe.booking.testfixture.TestFixture.createTestUnit;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * BookingControllerIntegrationTest class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentEventsHandler paymentEventsHandler;

    @AfterEach
    void tearDown() {
        paymentEventsHandler.cancelAllScheduledTasks();
        paymentRepository.deleteAll().block();
        bookingRepository.deleteAll().block();
        unitRepository.deleteAll().block();
        redissonClient.getKeys().flushall();
    }

    private Long unitId;
    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll().block();
        bookingRepository.deleteAll().block();
        unitRepository.deleteAll().block();
        redissonClient.getKeys().flushall();
        unitId = unitRepository.save(createTestUnit().build()).block().getId();
    }

    @Test
    void shouldCreateBookingSuccessfully_whenCreateBooking_givenValidRequestAndNoOverlappingBooking() {
        // given
        BookingRequest givenRequest = createBookingRequestForValidDates().unitId(unitId).build();

        // when
        BookingResponse response = webTestClient.post()
                                                .uri("/api/v1/bookings")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(givenRequest)
                                                .exchange()
                                                .expectStatus().isOk()
                                                .expectBody(BookingResponse.class)
                                                .returnResult()
                                                .getResponseBody();

        // then
        Booking expectedBooking = createExpectedBooking(unitId).build();

        Booking savedBooking = bookingRepository.findById(response.id()).block();
        assertThat(savedBooking)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(expectedBooking);

        Payment payment = paymentRepository.findByBookingId(savedBooking.getId()).block();
        Payment expectedPayment = createExpectedPayment().bookingId(savedBooking.getId())
                .build();
        assertThat(payment)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt", "expirationDate")
                .isEqualTo(expectedPayment);

        Unit updatedUnit = unitRepository.findById(unitId).block();
        assertThat(updatedUnit.isAvailable()).isFalse();
    }

    @Test
    void shouldReturnConflict_whenCreateBooking_givenOverlappingBookingExists() {
        // given
        Booking existingBooking = createOverlappingBooking().unitId(unitId).build();
        bookingRepository.save(existingBooking).block();

        BookingRequest givenRequest = createBookingRequestForValidDates().unitId(unitId).build();

        // when-then
        webTestClient.post()
                     .uri("/api/v1/bookings")
                     .contentType(MediaType.APPLICATION_JSON)
                     .bodyValue(givenRequest)
                     .exchange()
                     .expectStatus().isEqualTo(500);
    }

    @Test
    void shouldCancelBookingSuccessfully_whenCancelBooking_givenExistingPendingBooking() {
        // given
        Booking existingBooking = createExpectedBooking(unitId).status(BookingStatus.PENDING).build();
        Booking savedBooking = bookingRepository.save(existingBooking).block();
        Payment payment = createExpectedPayment()
                .bookingId(savedBooking.getId())
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment).block();

        BookingCancellationRequest cancellationRequest = createBookingCancellationRequest()
                .bookingId(savedBooking.getId())
                .build();

        // when
        BookingResponse response = webTestClient.post()
                                                .uri("/api/v1/bookings/cancel")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(cancellationRequest)
                                                .exchange()
                                                .expectStatus().isOk()
                                                .expectBody(BookingResponse.class)
                                                .returnResult()
                                                .getResponseBody();

        // then
        Booking expectedCancelledBooking = createExpectedBooking(unitId)
                .id(savedBooking.getId())
                .status(BookingStatus.CANCELLED)
                .build();

        Booking updatedBooking = bookingRepository.findById(savedBooking.getId()).block();
        assertThat(updatedBooking)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt", "totalCost")
                .isEqualTo(expectedCancelledBooking);

        Unit updatedUnit = unitRepository.findById(unitId).block();
        assertThat(updatedUnit.isAvailable()).isTrue();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("totalCost")
                .isEqualTo(BookingResponse.builder()
                                   .id(savedBooking.getId())
                                   .unitId(unitId)
                                   .userId(USER_ID)
                                   .startDate(START_DATE)
                                   .endDate(END_DATE)
                                   .status(BookingStatus.CANCELLED)
                                   .build());
    }
}
