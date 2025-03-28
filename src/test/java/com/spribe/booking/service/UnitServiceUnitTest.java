package com.spribe.booking.service;

import com.spribe.booking.dto.UnitCreateRequest;
import com.spribe.booking.dto.UnitResponse;
import com.spribe.booking.dto.UnitSearchRequest;
import com.spribe.booking.dto.UnitSearchResponse;
import com.spribe.booking.model.Unit;
import com.spribe.booking.repository.UnitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.spribe.booking.testfixture.TestFixture.UNIT_ID;
import static com.spribe.booking.testfixture.TestFixture.createTestUnit;
import static com.spribe.booking.testfixture.TestFixture.createUnitResponse;
import static com.spribe.booking.testfixture.TestFixture.createUnitSearchRequest;
import static com.spribe.booking.testfixture.TestFixture.createUnitSearchResponse;
import static com.spribe.booking.testfixture.TestFixture.createValidUnitRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitServiceUnitTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private UnitService unitService;

    @Captor
    private ArgumentCaptor<Unit> unitCaptor;

    @Test
    void shouldCreateUnitSuccessfully_whenCreateUnit_givenValidRequest() {
        // given
        UnitCreateRequest givenUnitCreateRequest = createValidUnitRequest().build();
        Unit expectedSavedUnit = createTestUnit().id(UNIT_ID).build();
        Unit givenUnit = createTestUnit().id(UNIT_ID).build();
        UnitResponse expectedUnitResponse = createUnitResponse(UNIT_ID).build();
        when(unitRepository.save(any(Unit.class))).thenReturn(Mono.just(expectedSavedUnit));
        when(cacheService.incrementAvailableUnits()).thenReturn(Mono.just(1L));

        // when
        Mono<UnitResponse> actual = unitService.createUnit(givenUnitCreateRequest);

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(response -> {
                        assertThat(response)
                                .usingRecursiveComparison()
                                .isEqualTo(expectedUnitResponse);
                        return true;
                    })
                    .verifyComplete();

        verify(unitRepository).save(unitCaptor.capture());
        Unit capturedUnit = unitCaptor.getValue();
        assertThat(capturedUnit)
                .usingRecursiveComparison()
                .ignoringFields("id", "updatedAt", "createdAt")
                .isEqualTo(givenUnit);

        verify(cacheService).incrementAvailableUnits();
    }

    @Test
    void shouldReturnSearchResults_whenSearchUnits_givenValidSearchRequest() {
        // given
        UnitSearchRequest givenSearchRequest = createUnitSearchRequest().build();
        UnitSearchResponse expectedSearchResponse = createUnitSearchResponse(1L).build();
        UnitSearchResponse expectedSearchResponse2 = createUnitSearchResponse(2L).build();
        when(unitRepository.searchUnits(
                any(), any(), anyLong(), anyInt(), anyString(),
                anyInt(), anyString(), anyString(), anyInt(), anyInt()
        )).thenReturn(Flux.just(expectedSearchResponse, expectedSearchResponse2));

        // when
        Flux<UnitSearchResponse> actual = unitService.searchUnits(givenSearchRequest);

        // then
        StepVerifier.create(actual)
                    .expectNext(expectedSearchResponse, expectedSearchResponse2)
                    .verifyComplete();

        verify(unitRepository).searchUnits(
                eq(givenSearchRequest.startDate()),
                eq(givenSearchRequest.endDate()),
                eq(givenSearchRequest.userId()),
                eq(givenSearchRequest.roomsNumber()),
                eq(givenSearchRequest.accommodationType().name()),
                eq(givenSearchRequest.floor()),
                eq(givenSearchRequest.sortBy().name()),
                eq(givenSearchRequest.sortDirection().name()),
                eq(givenSearchRequest.pageNo()),
                eq(givenSearchRequest.pageSize())
        );
    }

    @Test
    void shouldUpdateUnitAvailability_whenSetUnitAvailability_givenUnitIdAndAvailabilityFlag() {
        // given
        Unit givenUnit = createTestUnit().id(UNIT_ID).build();
        boolean givenAvailability = false;
        Unit expectedUpdatedUnit = createTestUnit().id(UNIT_ID).isAvailable(givenAvailability).build();
        UnitResponse expectedUpdatedResponse = createUnitResponse(UNIT_ID).isAvailable(givenAvailability).build();

        when(unitRepository.findById(UNIT_ID)).thenReturn(Mono.just(givenUnit));
        when(unitRepository.save(any(Unit.class))).thenReturn(Mono.just(expectedUpdatedUnit));

        // when
        Mono<UnitResponse> actual = unitService.setUnitAvailability(UNIT_ID, givenAvailability);

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(response -> {
                        assertThat(response)
                                .usingRecursiveComparison()
                                .isEqualTo(expectedUpdatedResponse);
                        return true;
                    })
                    .verifyComplete();

        verify(unitRepository).findById(UNIT_ID);
        verify(unitRepository).save(unitCaptor.capture());
        Unit capturedUnit = unitCaptor.getValue();
        assertThat(capturedUnit.getId()).isEqualTo(UNIT_ID);
        assertThat(capturedUnit.isAvailable()).isEqualTo(givenAvailability);
    }
}
