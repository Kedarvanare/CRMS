package com.crms.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String user;

    @Column(length = 20)
    private String type;

    @Column(length = 100)
    private String login;

    @Column(length = 100)
    @Builder.Default
    private String logout = "N/A";

    @Column(length = 50)
    private String ip;

    @Column(length = 20)
    private String status;

    @JsonProperty("created_at")
    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
