package com.spribe.booking.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Table("users")
public class User {
    @Id
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
