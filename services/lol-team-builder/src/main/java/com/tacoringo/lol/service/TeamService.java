package com.tacoringo.lol.service;

import com.tacoringo.lol.domain.Champion;
import com.tacoringo.lol.domain.Player;
import com.tacoringo.lol.domain.Team;
import com.tacoringo.lol.domain.TeamMember;
import com.tacoringo.lol.dto.CreateTeamRequest;
import com.tacoringo.lol.dto.TeamDto;
import com.tacoringo.lol.repository.PlayerRepository;
import com.tacoringo.lol.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public List<TeamDto> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(TeamDto::from)
                .collect(Collectors.toList());
    }

    public TeamDto getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));
        return TeamDto.from(team);
    }

    @Transactional
    public List<TeamDto> createBalancedTeams(CreateTeamRequest request) {
        List<Player> players = playerRepository.findAllById(request.getPlayerIds());

        if (players.size() < 2) {
            throw new RuntimeException("At least 2 players are required to create teams");
        }

        if (request.isAutoBalance()) {
            return createAutoBalancedTeams(players);
        } else {
            throw new RuntimeException("Manual team creation not yet implemented");
        }
    }

    private List<TeamDto> createAutoBalancedTeams(List<Player> players) {
        // 플레이어들을 스킬 레벨 순으로 정렬
        List<Player> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort(Comparator.comparing(Player::getSkillLevel).reversed());

        // 두 팀 생성
        Team blueTeam = Team.builder()
                .name("Blue Team")
                .color("BLUE")
                .members(new ArrayList<>())
                .build();

        Team redTeam = Team.builder()
                .name("Red Team")
                .color("RED")
                .members(new ArrayList<>())
                .build();

        // 포지션별로 플레이어를 그룹화
        Map<Champion.Position, List<Player>> playersByPosition = new HashMap<>();
        for (Champion.Position position : Champion.Position.values()) {
            playersByPosition.put(position, new ArrayList<>());
        }

        for (Player player : sortedPlayers) {
            playersByPosition.get(player.getPreferredPosition()).add(player);
        }

        // 각 포지션별로 플레이어를 두 팀에 배분
        for (Champion.Position position : Champion.Position.values()) {
            List<Player> positionPlayers = playersByPosition.get(position);
            for (int i = 0; i < positionPlayers.size(); i++) {
                Player player = positionPlayers.get(i);
                Team targetTeam = (i % 2 == 0) ? blueTeam : redTeam;

                TeamMember member = TeamMember.builder()
                        .player(player)
                        .assignedPosition(position)
                        .build();

                targetTeam.addMember(member);
            }
        }

        // 팀 저장
        Team savedBlueTeam = teamRepository.save(blueTeam);
        Team savedRedTeam = teamRepository.save(redTeam);

        return Arrays.asList(
                TeamDto.from(savedBlueTeam),
                TeamDto.from(savedRedTeam)
        );
    }

    @Transactional
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new RuntimeException("Team not found with id: " + id);
        }
        teamRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllTeams() {
        teamRepository.deleteAll();
    }
}
