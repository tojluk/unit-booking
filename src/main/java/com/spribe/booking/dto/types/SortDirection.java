package com.spribe.booking.dto.types;

import lombok.Getter;

/**
 * Enum representing the sort direction for sorting operations.
 */
@Getter
public enum SortDirection {
    ASC("ASC"),
    DESC("DESC");

    private final String direction;

    SortDirection(String direction) {
        this.direction = direction;
    }

}
