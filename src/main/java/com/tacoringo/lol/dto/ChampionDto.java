package com.tacoringo.lol.dto;

import com.tacoringo.lol.domain.Champion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChampionDto {
    private Long id;
    private String name;
    private String koreanName;
    private Champion.Position primaryPosition;
    private Champion.Position secondaryPosition;
    private Integer difficulty;
    private String description;

    public static ChampionDto from(Champion champion) {
        return ChampionDto.builder()
                .id(champion.getId())
                .name(champion.getName())
                .koreanName(champion.getKoreanName())
                .primaryPosition(champion.getPrimaryPosition())
                .secondaryPosition(champion.getSecondaryPosition())
                .difficulty(champion.getDifficulty())
                .description(champion.getDescription())
                .build();
    }

    public Champion toEntity() {
        return Champion.builder()
                .id(this.id)
                .name(this.name)
                .koreanName(this.koreanName)
                .primaryPosition(this.primaryPosition)
                .secondaryPosition(this.secondaryPosition)
                .difficulty(this.difficulty)
                .description(this.description)
                .build();
    }
}
