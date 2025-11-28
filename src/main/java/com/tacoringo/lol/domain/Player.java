package com.tacoringo.lol.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String summonerName;

    @Column(length = 50)
    private String realName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Champion.Position preferredPosition;

    @Column(nullable = false)
    private Boolean positionLocked = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "player_available_positions", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "position")
    @Builder.Default
    private Set<Champion.Position> availablePositions = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "player_unavailable_positions", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "position")
    @Builder.Default
    private Set<Champion.Position> unavailablePositions = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tier tier = Tier.SILVER;

    @Column(nullable = false)
    private Integer skillLevel; // 1-10

    @Column(length = 500)
    private String notes;

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

    public enum Tier {
        IRON("아이언", 1),
        BRONZE("브론즈", 2),
        SILVER("실버", 3),
        GOLD("골드", 4),
        PLATINUM("플래티넘", 5),
        EMERALD("에메랄드", 6),
        DIAMOND("다이아", 7),
        MASTER("마스터", 8),
        GRANDMASTER("그랜드마스터", 9),
        CHALLENGER("챌린저", 10);

        private final String koreanName;
        private final int value;

        Tier(String koreanName, int value) {
            this.koreanName = koreanName;
            this.value = value;
        }

        public String getKoreanName() {
            return koreanName;
        }

        public int getValue() {
            return value;
        }
    }
}
