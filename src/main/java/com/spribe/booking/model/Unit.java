package com.spribe.booking.model;

import com.spribe.booking.model.types.AccommodationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
