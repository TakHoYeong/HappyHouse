package com.tacoringo.lol.controller;

import com.tacoringo.lol.domain.Champion;
import com.tacoringo.lol.dto.PlayerDto;
import com.tacoringo.lol.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerDto>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDto> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    @GetMapping("/summoner/{summonerName}")
    public ResponseEntity<PlayerDto> getPlayerBySummonerName(@PathVariable String summonerName) {
        return ResponseEntity.ok(playerService.getPlayerBySummonerName(summonerName));
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<PlayerDto>> getPlayersByPosition(@PathVariable Champion.Position position) {
        return ResponseEntity.ok(playerService.getPlayersByPosition(position));
    }

    @PostMapping
    public ResponseEntity<PlayerDto> createPlayer(@RequestBody PlayerDto playerDto) {
        PlayerDto created = playerService.createPlayer(playerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDto> updatePlayer(
            @PathVariable Long id,
            @RequestBody PlayerDto playerDto) {
        return ResponseEntity.ok(playerService.updatePlayer(id, playerDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
