import { useState, useEffect } from 'react';
import axios from 'axios';
import './LolTeamBuilder.css';

const API_BASE_URL = 'http://localhost:8080/api/lol';

function LolTeamBuilder() {
  const [players, setPlayers] = useState([]);
  const [champions, setChampions] = useState([]);
  const [teams, setTeams] = useState([]);
  const [activeTab, setActiveTab] = useState('players');

  // Player Form State
  const [newPlayer, setNewPlayer] = useState({
    summonerName: '',
    realName: '',
    preferredPosition: 'TOP',
    secondaryPosition: 'JUNGLE',
    skillLevel: 5,
    notes: ''
  });

  // Champion Form State
  const [newChampion, setNewChampion] = useState({
    name: '',
    koreanName: '',
    primaryPosition: 'TOP',
    secondaryPosition: 'JUNGLE',
    difficulty: 5,
    description: ''
  });

  // Selected players for team creation
  const [selectedPlayers, setSelectedPlayers] = useState([]);

  useEffect(() => {
    fetchPlayers();
    fetchChampions();
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

  const fetchChampions = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/champions`);
      setChampions(response.data);
    } catch (error) {
      console.error('Error fetching champions:', error);
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
        secondaryPosition: 'JUNGLE',
        skillLevel: 5,
        notes: ''
      });
      fetchPlayers();
      alert('플레이어가 등록되었습니다!');
    } catch (error) {
      console.error('Error creating player:', error);
      alert('플레이어 등록에 실패했습니다.');
    }
  };

  const handleCreateChampion = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`${API_BASE_URL}/champions`, newChampion);
      setNewChampion({
        name: '',
        koreanName: '',
        primaryPosition: 'TOP',
        secondaryPosition: 'JUNGLE',
        difficulty: 5,
        description: ''
      });
      fetchChampions();
      alert('챔피언이 등록되었습니다!');
    } catch (error) {
      console.error('Error creating champion:', error);
      alert('챔피언 등록에 실패했습니다.');
    }
  };

  const handlePlayerSelection = (playerId) => {
    setSelectedPlayers(prev => {
      if (prev.includes(playerId)) {
        return prev.filter(id => id !== playerId);
      } else {
        return [...prev, playerId];
      }
    });
  };

  const handleCreateTeams = async () => {
    if (selectedPlayers.length < 2) {
      alert('최소 2명의 플레이어를 선택해주세요.');
      return;
    }

    try {
      await axios.post(`${API_BASE_URL}/teams/create`, {
        playerIds: selectedPlayers,
        autoBalance: true
      });
      setSelectedPlayers([]);
      fetchTeams();
      alert('팀이 생성되었습니다!');
    } catch (error) {
      console.error('Error creating teams:', error);
      alert('팀 생성에 실패했습니다.');
    }
  };

  const handleDeleteTeam = async (teamId) => {
    if (!confirm('정말 이 팀을 삭제하시겠습니까?')) {
      return;
    }

    try {
      await axios.delete(`${API_BASE_URL}/teams/${teamId}`);
      fetchTeams();
      alert('팀이 삭제되었습니다.');
    } catch (error) {
      console.error('Error deleting team:', error);
      alert('팀 삭제에 실패했습니다.');
    }
  };

  const handleDeleteAllTeams = async () => {
    if (!confirm('정말 모든 팀을 삭제하시겠습니까?')) {
      return;
    }

    try {
      await axios.delete(`${API_BASE_URL}/teams`);
      fetchTeams();
      alert('모든 팀이 삭제되었습니다.');
    } catch (error) {
      console.error('Error deleting all teams:', error);
      alert('팀 삭제에 실패했습니다.');
    }
  };

  const positions = ['TOP', 'JUNGLE', 'MID', 'ADC', 'SUPPORT'];

  return (
    <div className="lol-team-builder">
      <h1>LOL Team Builder</h1>

      <div className="tabs">
        <button
          className={activeTab === 'players' ? 'active' : ''}
          onClick={() => setActiveTab('players')}
        >
          플레이어 관리
        </button>
        <button
          className={activeTab === 'champions' ? 'active' : ''}
          onClick={() => setActiveTab('champions')}
        >
          챔피언 관리
        </button>
        <button
          className={activeTab === 'teams' ? 'active' : ''}
          onClick={() => setActiveTab('teams')}
        >
          팀 생성
        </button>
      </div>

      {activeTab === 'players' && (
        <div className="tab-content">
          <h2>플레이어 등록</h2>
          <form onSubmit={handleCreatePlayer} className="form">
            <input
              type="text"
              placeholder="소환사명"
              value={newPlayer.summonerName}
              onChange={(e) => setNewPlayer({...newPlayer, summonerName: e.target.value})}
              required
            />
            <input
              type="text"
              placeholder="실명"
              value={newPlayer.realName}
              onChange={(e) => setNewPlayer({...newPlayer, realName: e.target.value})}
            />
            <select
              value={newPlayer.preferredPosition}
              onChange={(e) => setNewPlayer({...newPlayer, preferredPosition: e.target.value})}
            >
              {positions.map(pos => <option key={pos} value={pos}>{pos}</option>)}
            </select>
            <select
              value={newPlayer.secondaryPosition}
              onChange={(e) => setNewPlayer({...newPlayer, secondaryPosition: e.target.value})}
            >
              {positions.map(pos => <option key={pos} value={pos}>{pos}</option>)}
            </select>
            <div>
              <label>실력 레벨: {newPlayer.skillLevel}</label>
              <input
                type="range"
                min="1"
                max="10"
                value={newPlayer.skillLevel}
                onChange={(e) => setNewPlayer({...newPlayer, skillLevel: parseInt(e.target.value)})}
              />
            </div>
            <textarea
              placeholder="메모"
              value={newPlayer.notes}
              onChange={(e) => setNewPlayer({...newPlayer, notes: e.target.value})}
            />
            <button type="submit">플레이어 등록</button>
          </form>

          <h2>플레이어 목록</h2>
          <div className="player-list">
            {players.map(player => (
              <div key={player.id} className="player-card">
                <h3>{player.summonerName}</h3>
                <p>실명: {player.realName || 'N/A'}</p>
                <p>선호 포지션: {player.preferredPosition}</p>
                <p>보조 포지션: {player.secondaryPosition}</p>
                <p>실력: {player.skillLevel}/10</p>
                {player.notes && <p>메모: {player.notes}</p>}
              </div>
            ))}
          </div>
        </div>
      )}

      {activeTab === 'champions' && (
        <div className="tab-content">
          <h2>챔피언 등록</h2>
          <form onSubmit={handleCreateChampion} className="form">
            <input
              type="text"
              placeholder="챔피언 이름 (영문)"
              value={newChampion.name}
              onChange={(e) => setNewChampion({...newChampion, name: e.target.value})}
              required
            />
            <input
              type="text"
              placeholder="챔피언 이름 (한글)"
              value={newChampion.koreanName}
              onChange={(e) => setNewChampion({...newChampion, koreanName: e.target.value})}
            />
            <select
              value={newChampion.primaryPosition}
              onChange={(e) => setNewChampion({...newChampion, primaryPosition: e.target.value})}
            >
              {positions.map(pos => <option key={pos} value={pos}>{pos}</option>)}
            </select>
            <select
              value={newChampion.secondaryPosition}
              onChange={(e) => setNewChampion({...newChampion, secondaryPosition: e.target.value})}
            >
              {positions.map(pos => <option key={pos} value={pos}>{pos}</option>)}
            </select>
            <div>
              <label>난이도: {newChampion.difficulty}</label>
              <input
                type="range"
                min="1"
                max="10"
                value={newChampion.difficulty}
                onChange={(e) => setNewChampion({...newChampion, difficulty: parseInt(e.target.value)})}
              />
            </div>
            <textarea
              placeholder="설명"
              value={newChampion.description}
              onChange={(e) => setNewChampion({...newChampion, description: e.target.value})}
            />
            <button type="submit">챔피언 등록</button>
          </form>

          <h2>챔피언 목록</h2>
          <div className="champion-list">
            {champions.map(champion => (
              <div key={champion.id} className="champion-card">
                <h3>{champion.name}</h3>
                <p>한글명: {champion.koreanName || 'N/A'}</p>
                <p>주 포지션: {champion.primaryPosition}</p>
                <p>보조 포지션: {champion.secondaryPosition}</p>
                <p>난이도: {champion.difficulty}/10</p>
                {champion.description && <p>설명: {champion.description}</p>}
              </div>
            ))}
          </div>
        </div>
      )}

      {activeTab === 'teams' && (
        <div className="tab-content">
          <h2>팀 생성</h2>
          <div className="team-creation">
            <h3>플레이어 선택</h3>
            <div className="player-selection">
              {players.map(player => (
                <label key={player.id} className="player-checkbox">
                  <input
                    type="checkbox"
                    checked={selectedPlayers.includes(player.id)}
                    onChange={() => handlePlayerSelection(player.id)}
                  />
                  {player.summonerName} ({player.preferredPosition}) - 실력: {player.skillLevel}/10
                </label>
              ))}
            </div>
            <button onClick={handleCreateTeams} className="create-team-btn">
              팀 생성 (선택: {selectedPlayers.length}명)
            </button>
          </div>

          <div className="teams-display">
            <div className="teams-header">
              <h2>생성된 팀</h2>
              {teams.length > 0 && (
                <button onClick={handleDeleteAllTeams} className="delete-all-btn">
                  모든 팀 삭제
                </button>
              )}
            </div>
            <div className="teams-container">
              {teams.map(team => (
                <div key={team.id} className={`team-card ${team.color.toLowerCase()}`}>
                  <div className="team-header">
                    <h3>{team.name}</h3>
                    <button onClick={() => handleDeleteTeam(team.id)} className="delete-btn">
                      삭제
                    </button>
                  </div>
                  <p>평균 실력: {team.averageSkillLevel?.toFixed(2)}/10</p>
                  <h4>팀 구성:</h4>
                  <ul>
                    {team.members.map(member => (
                      <li key={member.id}>
                        {member.assignedPosition}: {member.summonerName}
                        {member.championName && ` (${member.championName})`}
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default LolTeamBuilder;
