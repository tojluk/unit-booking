package com.spribe.booking.service;

import com.spribe.booking.dto.BookingRequest;
import com.spribe.booking.dto.BookingResponse;
import com.spribe.booking.helper.TestRLock;
import com.spribe.booking.model.Booking;
import com.spribe.booking.repository.BookingRepository;
import com.spribe.booking.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static com.spribe.booking.testfixture.TestFixture.END_DATE;
import static com.spribe.booking.testfixture.TestFixture.START_DATE;
import static com.spribe.booking.testfixture.TestFixture.TOTAL_COST;
import static com.spribe.booking.testfixture.TestFixture.UNIT_ID;
import static com.spribe.booking.testfixture.TestFixture.createBookingRequestForValidDates;
import static com.spribe.booking.testfixture.TestFixture.createBookingResponsePending;
import static com.spribe.booking.testfixture.TestFixture.createExpectedBooking;
import static com.spribe.booking.testfixture.TestFixture.createTestUnit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private CacheService cacheService;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private UnitService unitService;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private BookingService bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingSaveCaptor;

    @Captor
    private ArgumentCaptor<Booking> paymentBookingCaptor;

    @Captor
    private ArgumentCaptor<String> lockKeyCaptor;

    @BeforeEach
    void setUp() {
        // Setup mock lock
        TestRLock testLock = new TestRLock();
        when(redissonClient.getLock(anyString())).thenReturn(testLock);
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void shouldCreateBookingSuccessfully_whenCreateBooking_givenValidRequestAndNoOverlappingBooking() {
        // given
        BookingRequest givenRequest = createBookingRequestForValidDates().build();
        BookingResponse expected = createBookingResponsePending()
                .id(null)
                .totalCost(TOTAL_COST)
                .build();
        Booking expectedBooking = createExpectedBooking(null).build();

        when(bookingRepository.save(any())).thenAnswer(invocation -> Mono.just(createExpectedBooking(UNIT_ID).build()));
        when(unitRepository.findById(UNIT_ID)).thenReturn(Mono.just(createTestUnit().build()));
        when(bookingRepository.findOverlappingBookings(any(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.empty());
        when(paymentService.createPayment(any())).thenReturn(Mono.empty());
        when(unitService.setUnitAvailability(any(), eq(false))).thenReturn(Mono.empty());
        when(cacheService.incrementAvailableUnits()).thenReturn(Mono.empty());

        // when
        Mono<BookingResponse> result = bookingService.createBooking(givenRequest);

        // then
        StepVerifier.create(result)
                    .expectNextMatches(response -> response.equals(expected))
                    .verifyComplete();

        verify(unitRepository).findById(UNIT_ID);
        verify(bookingRepository).findOverlappingBookings(null, START_DATE, END_DATE);
        verify(bookingRepository).save(bookingSaveCaptor.capture());
        assertThat(bookingSaveCaptor.getValue()).usingRecursiveComparison()
                                                .ignoringActualNullFields()
                                                .isEqualTo(expectedBooking);
        verify(paymentService).createPayment(paymentBookingCaptor.capture());
        assertThat(paymentBookingCaptor.getValue()).usingRecursiveComparison()
                                                   .ignoringExpectedNullFields()
                                                   .isEqualTo(bookingSaveCaptor.getValue());
        verify(unitService).setUnitAvailability(null, false);
        verify(cacheService).incrementAvailableUnits();
        verify(redissonClient).getLock(lockKeyCaptor.capture());
        assertThat(lockKeyCaptor.getValue()).contains("unit:" + UNIT_ID);
        verifyNoMoreInteractions(paymentService);
        verifyNoMoreInteractions(cacheService);
    }
}
