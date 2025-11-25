package com.tacoringo.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTeamRequest {
    private List<Long> playerIds;
    private boolean autoBalance; // true면 자동으로 두 팀으로 밸런스 맞춰서 생성
}
