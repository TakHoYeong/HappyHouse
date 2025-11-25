package com.tacoringo.lol.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "champions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Champion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 20)
    private String koreanName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position primaryPosition;

    @Enumerated(EnumType.STRING)
    private Position secondaryPosition;

    @Column(nullable = false)
    private Integer difficulty; // 1-10

    @Column(length = 500)
    private String description;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Position {
        TOP, JUNGLE, MID, ADC, SUPPORT
    }
}
