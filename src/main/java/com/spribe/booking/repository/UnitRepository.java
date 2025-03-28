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
/**
 * Repository interface for managing Unit entities.
 * <p>
 * This interface extends ReactiveCrudRepository to provide CRUD operations for Unit entities.
 * It also includes a custom query method to search for units based on various criteria.
 * </p>
 */
@Repository
public interface UnitRepository extends ReactiveCrudRepository<Unit, Long> {

    Mono<Long> countByIsAvailable(Boolean isAvailable);

    /**
     * Searches for units based on various criteria.
     *
     * @param startDate        {@link LocalDate} The start date for the search.
     * @param endDate          {@link LocalDate} The end date for the search.
     * @param userId           {@link Integer}  The ID of the user.
     * @param roomsNumber      {@link Integer}  The number of rooms.
     * @param accommodationType {@link String}   The type of accommodation.
     * @param floor            {@link Integer}  The floor number.
     * @param sortBy           {@link String}   The field to sort by.
     * @param sortDirection    {@link String}   The direction to sort (ASC or DESC).
     * @param pageNo           {@link int}      The page number for pagination.
     * @param pageSize         {@link int}      The size of each page.
     * @return A Flux of UnitSearchResponse containing the search results.
     */
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
            @Param("userId") Long userId,
            @Param("roomsNumber") Integer roomsNumber,
            @Param("accommodationType") String accommodationType,
            @Param("floor") Integer floor,
            @Param("sortBy") String sortBy,
            @Param("sortDirection") String sortDirection,
            @Param("pageNo") int pageNo,
            @Param("pageSize") int pageSize
    );

}
