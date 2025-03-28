package com.spribe.booking.service;

import com.spribe.booking.dto.UnitCreateRequest;
import com.spribe.booking.dto.UnitResponse;
import com.spribe.booking.dto.UnitSearchRequest;
import com.spribe.booking.dto.UnitSearchResponse;
import com.spribe.booking.mapper.UnitMapper;
import com.spribe.booking.model.Unit;
import com.spribe.booking.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.spribe.booking.mapper.UnitMapper.mapUnitFromUnitCreateRequest;
import static java.util.Objects.isNull;
/**
 * Service class for managing units.
 * <p>
 * This class provides methods to create, search, and update unit availability.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final CacheService cacheService;

    /**
     * Creates a new unit.
     *
     * @param request {@link UnitCreateRequest} The request containing unit details.
     * @return A Mono containing the created unit response.
     */
    public Mono<UnitResponse> createUnit(UnitCreateRequest request) {
        Unit unit = mapUnitFromUnitCreateRequest(request);

        return unitRepository.save(unit)
                             .flatMap(savedUnit ->
                                              cacheService.incrementAvailableUnits()
                                                          .map(count -> savedUnit)
                             )
                             .map(UnitMapper::mapUnitToUnitResponse);
    }

    /**
     * Searches for units based on various criteria.
     *
     * @param request {@link UnitSearchRequest} The request containing search criteria.
     * @return A Flux of UnitSearchResponse containing the search results.
     */
    public Flux<UnitSearchResponse> searchUnits(UnitSearchRequest request) {
        return unitRepository.searchUnits(request.startDate(),
                                          request.endDate(),
                                          request.userId(),
                                          request.roomsNumber(),
                                          isNull(request.accommodationType()) ? null : request.accommodationType().name(),
                                          request.floor(),
                                          request.sortBy().name(),
                                          request.sortDirection().name(),
                                          request.pageNo(),
                                          request.pageSize());
    }

    /**
     * Updates the availability of a unit.
     *
     * @param id          {@link Long} The ID of the unit to update.
     * @param isAvailable {@link boolean} The new availability status.
     * @return A Mono containing the updated unit response.
     */
    public Mono<UnitResponse> setUnitAvailability(Long id, boolean isAvailable) {
        return unitRepository.findById(id)
                             .flatMap(existingUnit -> {
                                          existingUnit.setAvailable(isAvailable);
                                          return unitRepository.save(existingUnit);
                                      }
                             )
                             .map(UnitMapper::mapUnitToUnitResponse);
    }


}
