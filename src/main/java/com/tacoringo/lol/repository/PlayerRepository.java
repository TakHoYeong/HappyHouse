package com.tacoringo.lol.repository;

import com.tacoringo.lol.domain.Player;
import com.tacoringo.lol.domain.Champion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findBySummonerName(String summonerName);
    List<Player> findByPreferredPosition(Champion.Position position);
    List<Player> findBySkillLevelGreaterThanEqual(Integer skillLevel);
}
