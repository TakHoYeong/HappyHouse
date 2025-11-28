package com.tacoringo.lol.controller;

import com.tacoringo.lol.domain.Champion;
import com.tacoringo.lol.dto.ChampionDto;
import com.tacoringo.lol.service.ChampionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/champions")
@RequiredArgsConstructor
public class ChampionController {

    private final ChampionService championService;

    @GetMapping
    public ResponseEntity<List<ChampionDto>> getAllChampions() {
        return ResponseEntity.ok(championService.getAllChampions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChampionDto> getChampionById(@PathVariable Long id) {
        return ResponseEntity.ok(championService.getChampionById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ChampionDto> getChampionByName(@PathVariable String name) {
        return ResponseEntity.ok(championService.getChampionByName(name));
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<ChampionDto>> getChampionsByPosition(@PathVariable Champion.Position position) {
        return ResponseEntity.ok(championService.getChampionsByPosition(position));
    }

    @PostMapping
    public ResponseEntity<ChampionDto> createChampion(@RequestBody ChampionDto championDto) {
        ChampionDto created = championService.createChampion(championDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChampionDto> updateChampion(
            @PathVariable Long id,
            @RequestBody ChampionDto championDto) {
        return ResponseEntity.ok(championService.updateChampion(id, championDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChampion(@PathVariable Long id) {
        championService.deleteChampion(id);
        return ResponseEntity.noContent().build();
    }
}
