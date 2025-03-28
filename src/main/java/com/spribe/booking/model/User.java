package com.spribe.booking.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
/**
 * Represents a user in the booking system.
 * <p>
 * This class contains information about the user, including their ID, username, email,
 * and timestamps for creation and last update.
 * </p>
 */
@Getter
@Setter
@ToString
@Table("users")
public class User {
    @Id
    private Long id;
    private String username;
    private String email;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
