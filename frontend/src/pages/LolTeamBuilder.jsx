import { useState, useEffect } from 'react';
import axios from 'axios';
import './LolTeamBuilder.css';

const API_BASE_URL = 'http://localhost:8080';

const POSITIONS = [
  { value: 'TOP', label: '탑' },
  { value: 'JUNGLE', label: '정글' },
  { value: 'MID', label: '미드' },
  { value: 'ADC', label: '원딜' },
  { value: 'SUPPORT', label: '서폿' }
];

const TIERS = [
  { value: 'IRON', label: '아이언' },
  { value: 'BRONZE', label: '브론즈' },
  { value: 'SILVER', label: '실버' },
  { value: 'GOLD', label: '골드' },
  { value: 'PLATINUM', label: '플래티넘' },
  { value: 'EMERALD', label: '에메랄드' },
  { value: 'DIAMOND', label: '다이아' },
  { value: 'MASTER', label: '마스터' },
  { value: 'GRANDMASTER', label: '그랜드마스터' },
  { value: 'CHALLENGER', label: '챌린저' }
];

function LolTeamBuilder() {
  const [players, setPlayers] = useState([]);
  const [teams, setTeams] = useState([]);
  const [showPlayerForm, setShowPlayerForm] = useState(false);

  const [newPlayer, setNewPlayer] = useState({
    summonerName: '',
    realName: '',
    preferredPosition: 'TOP',
    positionLocked: false,
    availablePositions: [],
    unavailablePositions: [],
    tier: 'SILVER',
    skillLevel: 5,
    notes: ''
  });

  useEffect(() => {
    fetchPlayers();
    fetchTeams();
  }, []);

  const fetchPlayers = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/players`);
      setPlayers(response.data);
    } catch (error) {
      console.error('Error fetching players:', error);
    }
  };

  const fetchTeams = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/teams`);
      setTeams(response.data);
    } catch (error) {
      console.error('Error fetching teams:', error);
    }
  };

  const handleCreatePlayer = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`${API_BASE_URL}/players`, newPlayer);
      setNewPlayer({
        summonerName: '',
        realName: '',
        preferredPosition: 'TOP',
        positionLocked: false,
        availablePositions: [],
        unavailablePositions: [],
        tier: 'SILVER',
        skillLevel: 5,
        notes: ''
      });
      fetchPlayers();
      setShowPlayerForm(false);
      alert('플레이어가 등록되었습니다!');
    } catch (error) {
      console.error('Error creating player:', error);
      alert('플레이어 등록에 실패했습니다.');
    }
  };

  const handleDeletePlayer = async (playerId) => {
    if (!window.confirm('정말 이 플레이어를 삭제하시겠습니까?')) {
      return;
    }
    try {
      await axios.delete(`${API_BASE_URL}/players/${playerId}`);
      fetchPlayers();
      alert('플레이어가 삭제되었습니다.');
    } catch (error) {
      console.error('Error deleting player:', error);
      alert('플레이어 삭제에 실패했습니다.');
    }
  };

  const handleCreateTeams = async () => {
    if (players.length !== 10) {
      alert('정확히 10명의 플레이어가 필요합니다. 현재: ' + players.length + '명');
      return;
    }

    try {
      const playerIds = players.map(p => p.id);
      await axios.post(`${API_BASE_URL}/teams/create`, {
        playerIds: playerIds,
        autoBalance: true
      });
      fetchTeams();
      alert('팀이 생성되었습니다!');
    } catch (error) {
      console.error('Error creating teams:', error);
      alert('팀 생성에 실패했습니다: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleDeleteAllTeams = async () => {
    if (!window.confirm('정말 모든 팀을 삭제하시겠습니까?')) {
      return;
    }
    try {
      await axios.delete(`${API_BASE_URL}/teams`);
      fetchTeams();
      alert('모든 팀이 삭제되었습니다.');
    } catch (error) {
      console.error('Error deleting teams:', error);
      alert('팀 삭제에 실패했습니다.');
    }
  };

  const handlePositionCheckbox = (position, type) => {
    setNewPlayer(prev => {
      const list = type === 'available' ? prev.availablePositions : prev.unavailablePositions;
      const otherList = type === 'available' ? prev.unavailablePositions : prev.availablePositions;

      if (list.includes(position)) {
        return {
          ...prev,
          [type === 'available' ? 'availablePositions' : 'unavailablePositions']:
            list.filter(p => p !== position)
        };
      } else {
        // 다른 리스트에 있으면 제거
        const cleanedOtherList = otherList.filter(p => p !== position);
        return {
          ...prev,
          [type === 'available' ? 'availablePositions' : 'unavailablePositions']: [...list, position],
          [type === 'available' ? 'unavailablePositions' : 'availablePositions']: cleanedOtherList
        };
      }
    });
  };

  const getTierLabel = (tier) => {
    return TIERS.find(t => t.value === tier)?.label || tier;
  };

  const getPositionLabel = (position) => {
    return POSITIONS.find(p => p.value === position)?.label || position;
  };

  return (
    <div className="lol-team-builder">
      <header className="header">
        <h1>🎮 LOL 팀 밸런서</h1>
        <p>10명의 플레이어를 균형잡힌 5대5 팀으로 나눠보세요!</p>
      </header>

      <div className="main-content">
        <div className="left-panel">
          <div className="panel-header">
            <h2>플레이어 목록 ({players.length}/10)</h2>
            <button
              className="btn-primary"
              onClick={() => setShowPlayerForm(!showPlayerForm)}
            >
              {showPlayerForm ? '취소' : '+ 플레이어 추가'}
            </button>
          </div>

          {showPlayerForm && (
            <form onSubmit={handleCreatePlayer} className="player-form">
              <div className="form-group">
                <label>이름 *</label>
                <input
                  type="text"
                  placeholder="이름 입력"
                  value={newPlayer.summonerName}
                  onChange={(e) => setNewPlayer({...newPlayer, summonerName: e.target.value})}
                  required
                />
              </div>

              <div className="form-group">
                <label>티어 *</label>
                <select
                  value={newPlayer.tier}
                  onChange={(e) => setNewPlayer({...newPlayer, tier: e.target.value})}
                  className="tier-select"
                >
                  {TIERS.map(tier => (
                    <option key={tier.value} value={tier.value}>{tier.label}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="position-label">
                  주 포지션 *
                  <label className="checkbox-inline">
                    <input
                      type="checkbox"
                      checked={newPlayer.positionLocked}
                      onChange={(e) => setNewPlayer({...newPlayer, positionLocked: e.target.checked})}
                    />
                    포지션 고정
                  </label>
                </label>
                <select
                  value={newPlayer.preferredPosition}
                  onChange={(e) => setNewPlayer({...newPlayer, preferredPosition: e.target.value})}
                >
                  {POSITIONS.map(pos => (
                    <option key={pos.value} value={pos.value}>{pos.label}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>가능한 포지션</label>
                <div className="checkbox-group">
                  {POSITIONS.map(pos => (
                    <label key={pos.value} className="checkbox-label">
                      <input
                        type="checkbox"
                        checked={newPlayer.availablePositions.includes(pos.value)}
                        onChange={() => handlePositionCheckbox(pos.value, 'available')}
                      />
                      {pos.label}
                    </label>
                  ))}
                </div>
              </div>

              <div className="form-group">
                <label>불가능한 포지션</label>
                <div className="checkbox-group">
                  {POSITIONS.map(pos => (
                    <label key={pos.value} className="checkbox-label">
                      <input
                        type="checkbox"
                        checked={newPlayer.unavailablePositions.includes(pos.value)}
                        onChange={() => handlePositionCheckbox(pos.value, 'unavailable')}
                      />
                      {pos.label}
                    </label>
                  ))}
                </div>
              </div>

              <div className="form-actions">
                <button type="submit" className="btn-primary">등록</button>
                <button type="button" className="btn-secondary" onClick={() => setShowPlayerForm(false)}>
                  취소
                </button>
              </div>
            </form>
          )}

          <div className="player-list">
            {players.map(player => (
              <div key={player.id} className="player-card">
                <div className="player-header">
                  <h3>{player.summonerName}</h3>
                  <button
                    className="btn-delete"
                    onClick={() => handleDeletePlayer(player.id)}
                  >
                    ✕
                  </button>
                </div>
                <div className="player-info">
                  <span className={`tier-badge tier-${player.tier?.toLowerCase()}`}>
                    {getTierLabel(player.tier)}
                  </span>
                  <span className="position-badge">
                    {getPositionLabel(player.preferredPosition)}
                    {player.positionLocked && ' 🔒'}
                  </span>
                </div>
                {player.availablePositions?.length > 0 && (
                  <div className="position-info">
                    <small>가능: {player.availablePositions.map(p => getPositionLabel(p)).join(', ')}</small>
                  </div>
                )}
                {player.unavailablePositions?.length > 0 && (
                  <div className="position-info unavailable">
                    <small>불가: {player.unavailablePositions.map(p => getPositionLabel(p)).join(', ')}</small>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        <div className="right-panel">
          <div className="panel-header">
            <h2>팀 구성</h2>
            <div className="team-actions">
              <button
                className="btn-create-team"
                onClick={handleCreateTeams}
                disabled={players.length !== 10}
              >
                🎯 팀 생성
              </button>
              {teams.length > 0 && (
                <button
                  className="btn-delete-all"
                  onClick={handleDeleteAllTeams}
                >
                  전체 삭제
                </button>
              )}
            </div>
          </div>

          {teams.length === 0 ? (
            <div className="empty-state">
              <p>팀이 생성되지 않았습니다.</p>
              <p>10명의 플레이어를 등록한 후 팀을 생성하세요!</p>
            </div>
          ) : (
            <div className="teams-container">
              {teams.map(team => (
                <div key={team.id} className={`team-card team-${team.color.toLowerCase()}`}>
                  <div className="team-header">
                    <h3>{team.name}</h3>
                    <span className="team-avg">
                      평균: {team.averageSkillLevel?.toFixed(1) || 'N/A'}
                    </span>
                  </div>
                  <div className="team-members">
                    {team.members?.map(member => (
                      <div key={member.id} className="member-item">
                        <span className="member-position">
                          {getPositionLabel(member.assignedPosition)}
                        </span>
                        <span className="member-name">{member.summonerName}</span>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default LolTeamBuilder;
