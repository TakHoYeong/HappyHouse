package com.tacoringo.lol.dto;

import com.tacoringo.lol.domain.Champion;
import com.tacoringo.lol.domain.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberDto {
    private Long id;
    private Long playerId;
    private String summonerName;
    private Long championId;
    private String championName;
    private Champion.Position assignedPosition;

    public static TeamMemberDto from(TeamMember member) {
        return TeamMemberDto.builder()
                .id(member.getId())
                .playerId(member.getPlayer().getId())
                .summonerName(member.getPlayer().getSummonerName())
                .championId(member.getChampion() != null ? member.getChampion().getId() : null)
                .championName(member.getChampion() != null ? member.getChampion().getName() : null)
                .assignedPosition(member.getAssignedPosition())
                .build();
    }
}
