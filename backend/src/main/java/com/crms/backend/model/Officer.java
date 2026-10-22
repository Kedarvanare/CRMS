package com.crms.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "officers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Officer {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 255)
    @Builder.Default
    private String department = "";

    @Column(length = 20)
    @Builder.Default
    private String status = "Active";

    @Column(length = 20)
    @Builder.Default
    private String role = "Officer";

    @JsonProperty("created_at")
    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
