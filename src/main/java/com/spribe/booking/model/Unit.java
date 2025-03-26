package com.spribe.booking.model;

import com.spribe.booking.model.types.AccommodationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Represents a unit in the booking system.
 */
@Getter
@Setter
@ToString
@Table("units")
public class Unit {
    @Id
    private Long id;
    private Integer roomsNumber;
    private AccommodationType accommodationType;
    private Integer floor;
    private BigDecimal baseCost;
    private BigDecimal markupPercentage;
    private String description;

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
        if (baseCost == null || markupPercentage == null) {
            return BigDecimal.ZERO;
        }
        return baseCost.add(baseCost.multiply(markupPercentage.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));
    }
}
