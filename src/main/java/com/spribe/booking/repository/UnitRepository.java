package com.spribe.booking.repository;

import com.spribe.booking.dto.UnitSearchResponse;
import com.spribe.booking.model.Unit;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface UnitRepository extends ReactiveCrudRepository<Unit, Long> {

    Mono<Long> countByIsAvailable(Boolean isAvailable);

    @Query("""
           SELECT *
             FROM search_units(
                       :startDate, :endDate, :userId,
                       :roomsNumber, :accommodationType, :floor,
                       :sortBy, :sortDirection, :pageNo, :pageSize)
           """)
    Flux<UnitSearchResponse> searchUnits(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Integer userId,
            @Param("roomsNumber") Integer roomsNumber,
            @Param("accommodationType") String accommodationType,
            @Param("floor") Integer floor,
            @Param("sortBy") String sortBy,
            @Param("sortDirection") String sortDirection,
            @Param("pageNo") int pageNo,
            @Param("pageSize") int pageSize
    );

}
