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
        if (players.size() != 10) {
            throw new RuntimeException("정확히 10명의 플레이어가 필요합니다. 현재: " + players.size() + "명");
        }

        // 두 팀 생성
        Team blueTeam = Team.builder()
                .name("블루팀")
                .color("BLUE")
                .members(new ArrayList<>())
                .build();

        Team redTeam = Team.builder()
                .name("레드팀")
                .color("RED")
                .members(new ArrayList<>())
                .build();

        // 각 팀별 포지션 배정 맵
        Map<Team, Map<Champion.Position, Player>> teamAssignments = new HashMap<>();
        teamAssignments.put(blueTeam, new HashMap<>());
        teamAssignments.put(redTeam, new HashMap<>());

        // 사용되지 않은 플레이어 목록
        Set<Player> unassignedPlayers = new HashSet<>(players);

        // 1단계: 포지션이 고정된 플레이어를 먼저 배치
        List<Player> lockedPlayers = players.stream()
                .filter(Player::getPositionLocked)
                .sorted(Comparator.comparing((Player p) -> p.getTier().getValue()).reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < lockedPlayers.size(); i++) {
            Player player = lockedPlayers.get(i);
            Champion.Position position = player.getPreferredPosition();

            // 블루팀과 레드팀 중 해당 포지션이 비어있고 티어 합이 더 낮은 팀에 배치
            Team targetTeam = selectTeamForPosition(blueTeam, redTeam, position, teamAssignments);

            if (targetTeam != null) {
                teamAssignments.get(targetTeam).put(position, player);
                unassignedPlayers.remove(player);
            }
        }

        // 2단계: 남은 플레이어들을 티어 순으로 정렬하여 배치
        List<Player> remainingPlayers = new ArrayList<>(unassignedPlayers);
        remainingPlayers.sort(Comparator.comparing((Player p) -> p.getTier().getValue()).reversed());

        for (Player player : remainingPlayers) {
            boolean assigned = false;

            // 주 포지션 시도
            Team targetTeam = selectTeamForPosition(blueTeam, redTeam, player.getPreferredPosition(), teamAssignments);
            if (targetTeam != null && canPlayPosition(player, player.getPreferredPosition())) {
                teamAssignments.get(targetTeam).put(player.getPreferredPosition(), player);
                assigned = true;
            }

            // 가능한 포지션 시도
            if (!assigned && player.getAvailablePositions() != null) {
                for (Champion.Position position : Champion.Position.values()) {
                    if (player.getAvailablePositions().contains(position)) {
                        targetTeam = selectTeamForPosition(blueTeam, redTeam, position, teamAssignments);
                        if (targetTeam != null && canPlayPosition(player, position)) {
                            teamAssignments.get(targetTeam).put(position, player);
                            assigned = true;
                            break;
                        }
                    }
                }
            }

            // 그래도 안되면 빈 포지션 아무데나 배치
            if (!assigned) {
                for (Champion.Position position : Champion.Position.values()) {
                    targetTeam = selectTeamForPosition(blueTeam, redTeam, position, teamAssignments);
                    if (targetTeam != null && canPlayPosition(player, position)) {
                        teamAssignments.get(targetTeam).put(position, player);
                        assigned = true;
                        break;
                    }
                }
            }
        }

        // 3단계: 팀 멤버 생성
        for (Map.Entry<Team, Map<Champion.Position, Player>> teamEntry : teamAssignments.entrySet()) {
            Team team = teamEntry.getKey();
            Map<Champion.Position, Player> assignments = teamEntry.getValue();

            for (Map.Entry<Champion.Position, Player> assignment : assignments.entrySet()) {
                TeamMember member = TeamMember.builder()
                        .player(assignment.getValue())
                        .assignedPosition(assignment.getKey())
                        .build();
                team.addMember(member);
            }
        }

        // 팀 저장
        Team savedBlueTeam = teamRepository.save(blueTeam);
        Team savedRedTeam = teamRepository.save(redTeam);

        // 밸런스 상태 계산
        return calculateBalanceAndCreateDtos(savedBlueTeam, savedRedTeam);
    }

    private Team selectTeamForPosition(Team blueTeam, Team redTeam, Champion.Position position,
                                       Map<Team, Map<Champion.Position, Player>> teamAssignments) {
        boolean blueHasPosition = teamAssignments.get(blueTeam).containsKey(position);
        boolean redHasPosition = teamAssignments.get(redTeam).containsKey(position);

        if (blueHasPosition && redHasPosition) {
            return null; // 두 팀 모두 해당 포지션이 차있음
        }

        if (blueHasPosition) {
            return redTeam;
        }

        if (redHasPosition) {
            return blueTeam;
        }

        // 두 팀 모두 비어있으면 티어 합이 더 낮은 팀에 배치
        int blueTierSum = teamAssignments.get(blueTeam).values().stream()
                .mapToInt(p -> p.getTier().getValue())
                .sum();
        int redTierSum = teamAssignments.get(redTeam).values().stream()
                .mapToInt(p -> p.getTier().getValue())
                .sum();

        return blueTierSum <= redTierSum ? blueTeam : redTeam;
    }

    private boolean canPlayPosition(Player player, Champion.Position position) {
        if (player.getUnavailablePositions() != null &&
            player.getUnavailablePositions().contains(position)) {
            return false;
        }
        return true;
    }

    @Transactional
    public List<List<TeamDto>> createMultipleTeamOptions(CreateTeamRequest request) {
        List<Player> players = playerRepository.findAllById(request.getPlayerIds());

        if (players.size() != 10) {
            throw new RuntimeException("정확히 10명의 플레이어가 필요합니다. 현재: " + players.size() + "명");
        }

        List<List<TeamDto>> teamOptions = new ArrayList<>();

        // 3가지 다른 팀 구성 생성
        for (int i = 0; i < 3; i++) {
            List<TeamDto> teams = generateBalancedTeamsVariation(players, i);
            teamOptions.add(teams);
        }

        return teamOptions;
    }

    private List<TeamDto> generateBalancedTeamsVariation(List<Player> players, int variationIndex) {
        // 두 팀 생성 (DB에 저장하지 않음)
        Team blueTeam = Team.builder()
                .name("블루팀")
                .color("BLUE")
                .members(new ArrayList<>())
                .build();

        Team redTeam = Team.builder()
                .name("레드팀")
                .color("RED")
                .members(new ArrayList<>())
                .build();

        // 각 팀별 포지션 배정 맵
        Map<Team, Map<Champion.Position, Player>> teamAssignments = new HashMap<>();
        teamAssignments.put(blueTeam, new HashMap<>());
        teamAssignments.put(redTeam, new HashMap<>());

        // 사용되지 않은 플레이어 목록
        Set<Player> unassignedPlayers = new HashSet<>(players);

        // 1단계: 포지션이 고정된 플레이어를 먼저 배치
        List<Player> lockedPlayers = players.stream()
                .filter(Player::getPositionLocked)
                .sorted(Comparator.comparing((Player p) -> p.getTier().getValue()).reversed())
                .collect(Collectors.toList());

        for (Player player : lockedPlayers) {
            Champion.Position position = player.getPreferredPosition();
            Team targetTeam = selectTeamForPosition(blueTeam, redTeam, position, teamAssignments);

            if (targetTeam != null) {
                teamAssignments.get(targetTeam).put(position, player);
                unassignedPlayers.remove(player);
            }
        }

        // 2단계: 남은 플레이어들을 다른 전략으로 정렬하여 배치
        List<Player> remainingPlayers = new ArrayList<>(unassignedPlayers);

        // variationIndex에 따라 다른 정렬 전략 사용
        switch (variationIndex) {
            case 0: // 티어 내림차순
                remainingPlayers.sort(Comparator.comparing((Player p) -> p.getTier().getValue()).reversed());
                break;
            case 1: // 티어 오름차순
                remainingPlayers.sort(Comparator.comparing((Player p) -> p.getTier().getValue()));
                break;
            case 2: // 섞기
                Collections.shuffle(remainingPlayers);
                break;
        }

        for (Player player : remainingPlayers) {
            boolean assigned = false;

            // 주 포지션 시도
            Team targetTeam = selectTeamForPosition(blueTeam, redTeam, player.getPreferredPosition(), teamAssignments);
            if (targetTeam != null && canPlayPosition(player, player.getPreferredPosition())) {
                teamAssignments.get(targetTeam).put(player.getPreferredPosition(), player);
                assigned = true;
            }

            // 가능한 포지션 시도
            if (!assigned && player.getAvailablePositions() != null) {
                for (Champion.Position position : Champion.Position.values()) {
                    if (player.getAvailablePositions().contains(position)) {
                        targetTeam = selectTeamForPosition(blueTeam, redTeam, position, teamAssignments);
                        if (targetTeam != null && canPlayPosition(player, position)) {
                            teamAssignments.get(targetTeam).put(position, player);
                            assigned = true;
                            break;
                        }
                    }
                }
            }

            // 그래도 안되면 빈 포지션 아무데나 배치
            if (!assigned) {
                for (Champion.Position position : Champion.Position.values()) {
                    targetTeam = selectTeamForPosition(blueTeam, redTeam, position, teamAssignments);
                    if (targetTeam != null && canPlayPosition(player, position)) {
                        teamAssignments.get(targetTeam).put(position, player);
                        assigned = true;
                        break;
                    }
                }
            }
        }

        // 3단계: 팀 멤버 생성
        for (Map.Entry<Team, Map<Champion.Position, Player>> teamEntry : teamAssignments.entrySet()) {
            Team team = teamEntry.getKey();
            Map<Champion.Position, Player> assignments = teamEntry.getValue();

            for (Map.Entry<Champion.Position, Player> assignment : assignments.entrySet()) {
                TeamMember member = TeamMember.builder()
                        .player(assignment.getValue())
                        .assignedPosition(assignment.getKey())
                        .build();
                team.addMember(member);
            }
        }

        // DB에 저장하지 않고 DTO로 반환 (밸런스 계산 포함)
        return calculateBalanceForDtos(blueTeam, redTeam);
    }

    @Transactional
    public List<TeamDto> createRandomTeams(CreateTeamRequest request) {
        List<Player> players = playerRepository.findAllById(request.getPlayerIds());

        if (players.size() != 10) {
            throw new RuntimeException("정확히 10명의 플레이어가 필요합니다. 현재: " + players.size() + "명");
        }

        // 플레이어를 섞기
        List<Player> shuffledPlayers = new ArrayList<>(players);
        Collections.shuffle(shuffledPlayers);

        // 두 팀 생성
        Team blueTeam = Team.builder()
                .name("블루팀")
                .color("BLUE")
                .members(new ArrayList<>())
                .build();

        Team redTeam = Team.builder()
                .name("레드팀")
                .color("RED")
                .members(new ArrayList<>())
                .build();

        // 5명씩 나눠서 배치
        Champion.Position[] positions = Champion.Position.values();
        for (int i = 0; i < 5; i++) {
            TeamMember blueMember = TeamMember.builder()
                    .player(shuffledPlayers.get(i))
                    .assignedPosition(positions[i])
                    .build();
            blueTeam.addMember(blueMember);

            TeamMember redMember = TeamMember.builder()
                    .player(shuffledPlayers.get(i + 5))
                    .assignedPosition(positions[i])
                    .build();
            redTeam.addMember(redMember);
        }

        // 팀 저장
        Team savedBlueTeam = teamRepository.save(blueTeam);
        Team savedRedTeam = teamRepository.save(redTeam);

        // 밸런스 상태 계산
        return calculateBalanceAndCreateDtos(savedBlueTeam, savedRedTeam);
    }

    private List<TeamDto> calculateBalanceAndCreateDtos(Team team1, Team team2) {
        double avg1 = team1.getMembers().stream()
                .mapToDouble(m -> m.getPlayer().getTier().getValue())
                .average()
                .orElse(0.0);

        double avg2 = team2.getMembers().stream()
                .mapToDouble(m -> m.getPlayer().getTier().getValue())
                .average()
                .orElse(0.0);

        double diff = Math.abs(avg1 - avg2);
        String status1, status2;

        if (diff <= 0.5) {
            status1 = status2 = "약우세";
        } else if (diff <= 1.0) {
            status1 = avg1 > avg2 ? "우세" : "";
            status2 = avg2 > avg1 ? "우세" : "";
        } else if (diff <= 2.0) {
            status1 = avg1 > avg2 ? "강우세" : "";
            status2 = avg2 > avg1 ? "강우세" : "";
        } else {
            status1 = avg1 > avg2 ? "천국" : "지옥";
            status2 = avg2 > avg1 ? "천국" : "지옥";
        }

        return Arrays.asList(
                TeamDto.fromWithBalance(team1, status1),
                TeamDto.fromWithBalance(team2, status2)
        );
    }

    private List<TeamDto> calculateBalanceForDtos(Team team1, Team team2) {
        double avg1 = team1.getMembers().stream()
                .mapToDouble(m -> m.getPlayer().getTier().getValue())
                .average()
                .orElse(0.0);

        double avg2 = team2.getMembers().stream()
                .mapToDouble(m -> m.getPlayer().getTier().getValue())
                .average()
                .orElse(0.0);

        double diff = Math.abs(avg1 - avg2);
        String status1, status2;

        if (diff <= 0.5) {
            status1 = status2 = "약우세";
        } else if (diff <= 1.0) {
            status1 = avg1 > avg2 ? "우세" : "";
            status2 = avg2 > avg1 ? "우세" : "";
        } else if (diff <= 2.0) {
            status1 = avg1 > avg2 ? "강우세" : "";
            status2 = avg2 > avg1 ? "강우세" : "";
        } else {
            status1 = avg1 > avg2 ? "천국" : "지옥";
            status2 = avg2 > avg1 ? "천국" : "지옥";
        }

        return Arrays.asList(
                TeamDto.fromWithBalance(team1, status1),
                TeamDto.fromWithBalance(team2, status2)
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
