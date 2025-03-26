package com.spribe.booking.controller;


import com.spribe.booking.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/cache")
@RequiredArgsConstructor
@Tag(name = "CachedData", description = "API controlling cached data")
public class CacheController {

    private final CacheService cacheService;

    @GetMapping
    @Operation(summary = "Available Units Count")
    public Mono<Long> getAvailableUnitsCount() {
        return cacheService.getAvailableUnitsCount();
    }

    @DeleteMapping
    @Operation(summary = "Cache evict")
    public Mono<Void> clearCache() {
        return cacheService.clearCache();
    }

}
