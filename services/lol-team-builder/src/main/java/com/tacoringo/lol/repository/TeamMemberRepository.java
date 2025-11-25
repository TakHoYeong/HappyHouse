package com.tacoringo.lol.repository;

import com.tacoringo.lol.domain.TeamMember;
import com.tacoringo.lol.domain.Champion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByTeamId(Long teamId);
    List<TeamMember> findByPlayerId(Long playerId);
    List<TeamMember> findByAssignedPosition(Champion.Position position);
}
