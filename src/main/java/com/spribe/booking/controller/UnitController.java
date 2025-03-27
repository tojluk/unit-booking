package com.spribe.booking.controller;

import com.spribe.booking.dto.UnitCreateRequest;
import com.spribe.booking.dto.UnitResponse;
import com.spribe.booking.dto.UnitSearchRequest;
import com.spribe.booking.dto.UnitSearchResponse;
import com.spribe.booking.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
@Tag(name = "Units", description = "API controlling Units")
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    @Operation(summary = "Create new unit")
    public Mono<UnitResponse> createUnit(@RequestBody UnitCreateRequest request) {
        return unitService.createUnit(request);
    }

    @PostMapping("/search")
    @Operation(summary = "Search Units by criteria")
    public Flux<UnitSearchResponse> searchUnits(@RequestBody UnitSearchRequest request) {
        return unitService.searchUnits(request);
    }
}
