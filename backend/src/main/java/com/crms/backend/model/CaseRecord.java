package com.crms.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cases_tbl")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseRecord {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(nullable = false, length = 100)
    private String complainer;

    @Column(length = 100)
    @Builder.Default
    private String criminal = "Unknown";

    @JsonProperty("date_reported")
    @Column(name = "date_reported")
    private LocalDate dateReported;

    @Column(length = 20)
    @Builder.Default
    private String status = "Pending";

    @Column(length = 255)
    private String location;

    @JsonProperty("complainer_aadhar")
    @Column(name = "complainer_aadhar", length = 20)
    private String complainerAadhar;

    @JsonProperty("created_at")
    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
