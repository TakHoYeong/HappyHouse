package com.tacoringo.lol.controller;

import com.tacoringo.lol.dto.CreateTeamRequest;
import com.tacoringo.lol.dto.TeamDto;
import com.tacoringo.lol.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamDto>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<List<TeamDto>> createBalancedTeams(@RequestBody CreateTeamRequest request) {
        List<TeamDto> teams = teamService.createBalancedTeams(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(teams);
    }

    @PostMapping("/create/multiple")
    public ResponseEntity<List<List<TeamDto>>> createMultipleTeamOptions(@RequestBody CreateTeamRequest request) {
        List<List<TeamDto>> teamOptions = teamService.createMultipleTeamOptions(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamOptions);
    }

    @PostMapping("/create/random")
    public ResponseEntity<List<TeamDto>> createRandomTeams(@RequestBody CreateTeamRequest request) {
        List<TeamDto> teams = teamService.createRandomTeams(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(teams);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllTeams() {
        teamService.deleteAllTeams();
        return ResponseEntity.noContent().build();
    }
}
