package com.spribe.booking.dto.types;

public enum SortDirection {
    ASC("ASC"),
    DESC("DESC");

    private final String direction;

    SortDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }
}
