package com.tacoringo.lol.dto;

import com.tacoringo.lol.domain.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDto {
    private Long id;
    private String name;
    private String color;
    private List<TeamMemberDto> members;
    private Double averageSkillLevel;
    private Double averageTierScore;
    private String balanceStatus;

    public static TeamDto from(Team team) {
        double avgTierScore = team.getMembers().stream()
                .mapToDouble(member -> member.getPlayer().getTier().getValue())
                .average()
                .orElse(0.0);

        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .color(team.getColor())
                .members(team.getMembers().stream()
                        .map(TeamMemberDto::from)
                        .collect(Collectors.toList()))
                .averageSkillLevel(team.getAverageSkillLevel())
                .averageTierScore(avgTierScore)
                .build();
    }

    public static TeamDto fromWithBalance(Team team, String balanceStatus) {
        TeamDto dto = from(team);
        dto.setBalanceStatus(balanceStatus);
        return dto;
    }
}
