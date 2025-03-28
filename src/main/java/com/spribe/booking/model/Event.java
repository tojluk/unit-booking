package com.spribe.booking.model;

import com.spribe.booking.model.types.EventType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
/**
 * Represents an event in the booking system.
 * <p>
 * This class contains information about the event, including the entity type, entity ID,
 * event type, description, and timestamp for creation.
 * </p>
 */
@Getter
@Setter
@ToString
@Table("events")
public class Event {
    @Id
    private Long id;
    private String entityType;
    private Long entityId;
    private EventType eventType;
    private String description;

    @CreatedDate
    private LocalDateTime createdAt;
}
