package com.tacoringo.lol.dto;

import com.tacoringo.lol.domain.Champion;
import com.tacoringo.lol.domain.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDto {
    private Long id;
    private String summonerName;
    private String realName;
    private Champion.Position preferredPosition;
    private Champion.Position secondaryPosition;
    private Integer skillLevel;
    private String notes;

    public static PlayerDto from(Player player) {
        return PlayerDto.builder()
                .id(player.getId())
                .summonerName(player.getSummonerName())
                .realName(player.getRealName())
                .preferredPosition(player.getPreferredPosition())
                .secondaryPosition(player.getSecondaryPosition())
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
                .secondaryPosition(this.secondaryPosition)
                .skillLevel(this.skillLevel)
                .notes(this.notes)
                .build();
    }
}
