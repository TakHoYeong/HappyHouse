package com.tacoringo.lol.repository;

import com.tacoringo.lol.domain.Champion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChampionRepository extends JpaRepository<Champion, Long> {
    Optional<Champion> findByName(String name);
    List<Champion> findByPrimaryPosition(Champion.Position position);
    List<Champion> findByDifficultyLessThanEqual(Integer difficulty);
}
