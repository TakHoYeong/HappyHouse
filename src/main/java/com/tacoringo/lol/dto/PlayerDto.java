package com.tacoringo.lol.dto;

import com.tacoringo.lol.domain.Champion;
import com.tacoringo.lol.domain.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDto {
    private Long id;
    private String summonerName;
    private String realName;
    private Champion.Position preferredPosition;
    private Boolean positionLocked;
    @Builder.Default
    private Set<Champion.Position> availablePositions = new HashSet<>();
    @Builder.Default
    private Set<Champion.Position> unavailablePositions = new HashSet<>();
    private Player.Tier tier;
    private Integer skillLevel;
    private String notes;

    public static PlayerDto from(Player player) {
        return PlayerDto.builder()
                .id(player.getId())
                .summonerName(player.getSummonerName())
                .realName(player.getRealName())
                .preferredPosition(player.getPreferredPosition())
                .positionLocked(player.getPositionLocked())
                .availablePositions(player.getAvailablePositions() != null ?
                    new HashSet<>(player.getAvailablePositions()) : new HashSet<>())
                .unavailablePositions(player.getUnavailablePositions() != null ?
                    new HashSet<>(player.getUnavailablePositions()) : new HashSet<>())
                .tier(player.getTier())
                .skillLevel(player.getSkillLevel())
                .notes(player.getNotes())
                .build();
    }

    public Player toEntity() {
        return Player.builder()
                .id(this.id)
                .summonerName(this.summonerName)
                .realName(this.realName)
                .preferredPosition(this.preferredPosition)
                .positionLocked(this.positionLocked != null ? this.positionLocked : false)
                .availablePositions(this.availablePositions != null ?
                    new HashSet<>(this.availablePositions) : new HashSet<>())
                .unavailablePositions(this.unavailablePositions != null ?
                    new HashSet<>(this.unavailablePositions) : new HashSet<>())
                .tier(this.tier != null ? this.tier : Player.Tier.SILVER)
                .skillLevel(this.skillLevel)
                .notes(this.notes)
                .build();
    }
}
