package com.crms.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "criminals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Criminal {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String crime;

    @Column(length = 20)
    @Builder.Default
    private String status = "Wanted";

    @JsonProperty("sentenceEnd")
    @Column(name = "sentenceEnd")
    private LocalDate sentenceEnd;

    private Integer age;

    @Column(length = 10)
    private String gender;

    @Column(length = 100)
    private String occupation;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    @Builder.Default
    private String officer = "Unknown";

    @JsonProperty("created_at")
    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
