package com.spribe.booking.model;

import com.spribe.booking.model.types.AccommodationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

/**
 * Represents a unit in the booking system.
 */
@Getter
@Setter
@ToString
@Table("units")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Unit {
    @Id
    private Long id;
    private Integer roomsNumber;
    private AccommodationType accommodationType;
    private Integer floor;
    private BigDecimal baseCost;
    private BigDecimal markupPercentage;
    private String description;
    //TODO: switch to range of booking dates
    private boolean isAvailable;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Calculates the total cost of the unit based on the base cost and markup percentage.
     *
     * @return the total cost as a BigDecimal
     */
    public BigDecimal calculateTotalCost() {
        if (isNull(baseCost) || isNull(markupPercentage)) {
            return BigDecimal.ZERO;
        }
        return baseCost.add(baseCost.multiply(markupPercentage.divide(BigDecimal.valueOf(100),
                                                                      2,
                                                                      RoundingMode.HALF_UP)));
    }
}
