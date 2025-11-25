package com.tacoringo.lol.service;

import com.tacoringo.lol.domain.Champion;
import com.tacoringo.lol.dto.ChampionDto;
import com.tacoringo.lol.repository.ChampionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChampionService {

    private final ChampionRepository championRepository;

    public List<ChampionDto> getAllChampions() {
        return championRepository.findAll().stream()
                .map(ChampionDto::from)
                .collect(Collectors.toList());
    }

    public ChampionDto getChampionById(Long id) {
        Champion champion = championRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Champion not found with id: " + id));
        return ChampionDto.from(champion);
    }

    public ChampionDto getChampionByName(String name) {
        Champion champion = championRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Champion not found with name: " + name));
        return ChampionDto.from(champion);
    }

    public List<ChampionDto> getChampionsByPosition(Champion.Position position) {
        return championRepository.findByPrimaryPosition(position).stream()
                .map(ChampionDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChampionDto createChampion(ChampionDto championDto) {
        Champion champion = championDto.toEntity();
        Champion saved = championRepository.save(champion);
        return ChampionDto.from(saved);
    }

    @Transactional
    public ChampionDto updateChampion(Long id, ChampionDto championDto) {
        Champion champion = championRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Champion not found with id: " + id));

        champion.setName(championDto.getName());
        champion.setKoreanName(championDto.getKoreanName());
        champion.setPrimaryPosition(championDto.getPrimaryPosition());
        champion.setSecondaryPosition(championDto.getSecondaryPosition());
        champion.setDifficulty(championDto.getDifficulty());
        champion.setDescription(championDto.getDescription());

        Champion updated = championRepository.save(champion);
        return ChampionDto.from(updated);
    }

    @Transactional
    public void deleteChampion(Long id) {
        if (!championRepository.existsById(id)) {
            throw new RuntimeException("Champion not found with id: " + id);
        }
        championRepository.deleteById(id);
    }
}
