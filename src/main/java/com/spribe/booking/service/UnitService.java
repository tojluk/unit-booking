package com.spribe.booking.service;

import com.spribe.booking.dto.UnitCreateRequest;
import com.spribe.booking.dto.UnitResponse;
import com.spribe.booking.dto.UnitSearchRequest;
import com.spribe.booking.mapper.UnitMapper;
import com.spribe.booking.model.Unit;
import com.spribe.booking.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.spribe.booking.mapper.UnitMapper.mapUnitFromUnitCreateRequest;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final CacheService cacheService;

    public Mono<UnitResponse> createUnit(UnitCreateRequest request) {
        Unit unit = mapUnitFromUnitCreateRequest(request);

        return unitRepository.save(unit)
                             .flatMap(savedUnit ->
                                              cacheService.incrementAvailableUnits()
                                                          .map(count -> savedUnit)
                             )
                             .map(UnitMapper::mapUnitToUnitResponse);
    }

    //TODO: add pagination
    public Flux<UnitResponse> searchUnits(UnitSearchRequest request) {
          return unitRepository.findAll()
                             .skip((long) request.page() * request.size())
                             .take(request.size())
                             .map(UnitMapper::mapUnitToUnitResponse);
    }


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
