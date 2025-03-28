package com.spribe.booking.controller;


import com.spribe.booking.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
/**
 * CacheController is a REST controller that handles cache-related operations.
 * It provides an endpoint for retrieving the count of available units.
 */
@RestController
@RequestMapping("/api/v1/cache")
@RequiredArgsConstructor
@Tag(name = "CachedData", description = "API controller cached data")
public class CacheController {

    private final CacheService cacheService;

    @GetMapping
    @Operation(summary = "Available Units Count")
    public Mono<Long> getAvailableUnitsCount() {
        return cacheService.getAvailableUnitsCount();
    }

}
