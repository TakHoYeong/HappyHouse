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

    public static TeamDto from(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .color(team.getColor())
                .members(team.getMembers().stream()
                        .map(TeamMemberDto::from)
                        .collect(Collectors.toList()))
                .averageSkillLevel(team.getAverageSkillLevel())
                .build();
    }
}
