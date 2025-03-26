package com.spribe.booking.service;

import com.spribe.booking.dto.UnitCreateRequest;
import com.spribe.booking.dto.UnitResponse;
import com.spribe.booking.dto.UnitSearchRequest;
import com.spribe.booking.model.Unit;
import com.spribe.booking.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepository unitRepository;
    private final CacheService cacheService;

    public Mono<UnitResponse> createUnit(UnitCreateRequest request) {
        Unit unit = new Unit();
        unit.setRoomsNumber(request.roomsNumber());
        unit.setAccommodationType(request.accommodationType());
        unit.setFloor(request.floor());
        unit.setBaseCost(request.baseCost());
        unit.setDescription(request.description());
        unit.setMarkupPercentage(BigDecimal.valueOf(15)); // TODO: Set from config
        unit.setCreatedAt(LocalDateTime.now());

        return unitRepository.save(unit)
                             .then(cacheService.incrementAvailableUnits().thenReturn(unit))
                             .map(this::mapToResponse);
    }

    //TODO: add pagination
    public Flux<UnitResponse> searchUnits(UnitSearchRequest request) {
          return unitRepository.findAll()
                             .skip((long) request.page() * request.size())
                             .take(request.size())
                             .map(this::mapToResponse);
    }

    //TODO: move to mapper layer
    private UnitResponse mapToResponse(Unit unit) {
        return new UnitResponse(
                unit.getId(),
                unit.getRoomsNumber(),
                unit.getAccommodationType(),
                unit.getFloor(),
                unit.getBaseCost(),
                unit.calculateTotalCost(),
                //TODO: add isAvailable logic
                true,
                unit.getDescription(),
                unit.getCreatedAt(),
                unit.getUpdatedAt()
        );
    }
}
