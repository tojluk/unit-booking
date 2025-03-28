package com.spribe.booking.dto.types;

import lombok.Getter;

/**
 * Enum representing the fields by which units can be sorted.
 */
@Getter
public enum UnitSortField {
    ID("id"),
    NAME("name"),
    FLOOR("floor"),
    ROOMS_NUMBER("rooms_number"),
    COST("cost"),
    CREATED_AT("created_at");

    private final String fieldName;

    UnitSortField(String fieldName) {
        this.fieldName = fieldName;
    }

}
