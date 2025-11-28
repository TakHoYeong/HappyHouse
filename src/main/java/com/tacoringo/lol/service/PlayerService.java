package com.tacoringo.lol.service;

import com.tacoringo.lol.domain.Champion;
import com.tacoringo.lol.domain.Player;
import com.tacoringo.lol.dto.PlayerDto;
import com.tacoringo.lol.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

    private final PlayerRepository playerRepository;

    public List<PlayerDto> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(PlayerDto::from)
                .collect(Collectors.toList());
    }

    public PlayerDto getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
        return PlayerDto.from(player);
    }

    public PlayerDto getPlayerBySummonerName(String summonerName) {
        Player player = playerRepository.findBySummonerName(summonerName)
                .orElseThrow(() -> new RuntimeException("Player not found with summoner name: " + summonerName));
        return PlayerDto.from(player);
    }

    public List<PlayerDto> getPlayersByPosition(Champion.Position position) {
        return playerRepository.findByPreferredPosition(position).stream()
                .map(PlayerDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlayerDto createPlayer(PlayerDto playerDto) {
        Player player = playerDto.toEntity();
        Player saved = playerRepository.save(player);
        return PlayerDto.from(saved);
    }

    @Transactional
    public PlayerDto updatePlayer(Long id, PlayerDto playerDto) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));

        player.setSummonerName(playerDto.getSummonerName());
        player.setRealName(playerDto.getRealName());
        player.setPreferredPosition(playerDto.getPreferredPosition());
        player.setSecondaryPosition(playerDto.getSecondaryPosition());
        player.setSkillLevel(playerDto.getSkillLevel());
        player.setNotes(playerDto.getNotes());

        Player updated = playerRepository.save(player);
        return PlayerDto.from(updated);
    }

    @Transactional
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new RuntimeException("Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
    }
}
