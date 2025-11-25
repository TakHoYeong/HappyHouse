# HappyHouse - Microservices Architecture

React와 Spring Boot를 사용한 마이크로서비스 아키텍처 기반 웹 애플리케이션입니다.

## 프로젝트 개요

HappyHouse는 MSA(Microservices Architecture) 구조로 설계된 프로젝트로, 각 서비스가 독립적으로 개발 및 배포될 수 있습니다.

### 주요 서비스

1. **API Gateway** (Port: 8080)
   - Spring Cloud Gateway 기반
   - 모든 클라이언트 요청의 진입점
   - 각 마이크로서비스로 라우팅

2. **LOL Team Builder Service** (Port: 8081)
   - 리그 오브 레전드 팀 구성 서비스
   - 플레이어 관리
   - 챔피언 관리
   - 자동 팀 밸런싱 기능

3. **User Service** (Port: 8082)
   - 사용자 관리 서비스
   - 사용자 CRUD 기능

## 기술 스택

### 백엔드
- **Java**: 21
- **Spring Boot**: 3.5.7
- **Spring Cloud Gateway**: API Gateway
- **Spring Data JPA**: 데이터 접근 계층
- **Hibernate**: ORM
- **MariaDB**: 11.2 (각 서비스별 독립 DB)
- **Lombok**: 보일러플레이트 코드 감소
- **Gradle**: 빌드 도구

### 프론트엔드
- **React**: 18
- **Vite**: 최신 버전
- **Axios**: HTTP 클라이언트

### 인프라
- **Docker & Docker Compose**: 데이터베이스 컨테이너 관리

## 프로젝트 구조

```
HappyHouse/
├── services/
│   ├── api-gateway/              # API Gateway (Port: 8080)
│   │   ├── src/
│   │   ├── build.gradle
│   │   └── settings.gradle
│   ├── lol-team-builder/         # LOL Team Builder Service (Port: 8081)
│   │   ├── src/
│   │   │   ├── main/java/com/tacoringo/lol/
│   │   │   │   ├── domain/       # Champion, Player, Team 엔티티
│   │   │   │   ├── repository/   # JPA Repository
│   │   │   │   ├── service/      # 비즈니스 로직
│   │   │   │   ├── controller/   # REST API
│   │   │   │   └── dto/          # Data Transfer Objects
│   │   ├── build.gradle
│   │   └── settings.gradle
│   └── user-service/             # User Service (Port: 8082)
│       ├── src/
│       ├── build.gradle
│       └── settings.gradle
├── frontend/                     # React 프론트엔드
│   ├── src/
│   │   ├── pages/
│   │   │   └── LolTeamBuilder.jsx
│   │   └── App.jsx
│   ├── package.json
│   └── vite.config.js
├── docker-compose.yml
└── README.md
```

## 시작하기

### 사전 요구사항

- **Java 21** 설치
- **Node.js** 22.x 이상
- **Docker & Docker Compose** 설치
- **Gradle** (또는 내장된 Gradle Wrapper 사용)

### 1. 데이터베이스 실행

Docker Compose를 사용하여 각 서비스별 MariaDB를 실행합니다:

```bash
docker-compose up -d
```

실행되는 데이터베이스:
- **LOL Team Builder DB**: localhost:3306 (lol_team_builder)
- **User Service DB**: localhost:3307 (user_service)

### 2. 백엔드 서비스 실행

각 서비스를 개별적으로 실행할 수 있습니다:

#### API Gateway 실행
```bash
cd services/api-gateway
../../gradlew bootRun
# 또는
../../gradlew build
java -jar build/libs/api-gateway-0.0.1-SNAPSHOT.jar
```

#### LOL Team Builder Service 실행
```bash
cd services/lol-team-builder
../../gradlew bootRun
# 또는
../../gradlew build
java -jar build/libs/lol-team-builder-0.0.1-SNAPSHOT.jar
```

#### User Service 실행
```bash
cd services/user-service
../../gradlew bootRun
# 또는
../../gradlew build
java -jar build/libs/user-service-0.0.1-SNAPSHOT.jar
```

### 3. 프론트엔드 실행

```bash
cd frontend
npm install  # 처음 한 번만 실행
npm run dev
```

프론트엔드 개발 서버는 `http://localhost:5173`에서 실행됩니다.

## API 엔드포인트

모든 API는 API Gateway(`http://localhost:8080`)를 통해 접근합니다.

### LOL Team Builder API

Base URL: `http://localhost:8080/api/lol`

#### Players (플레이어)
- `GET /api/lol/players` - 모든 플레이어 조회
- `GET /api/lol/players/{id}` - 특정 플레이어 조회
- `GET /api/lol/players/summoner/{summonerName}` - 소환사명으로 조회
- `POST /api/lol/players` - 새 플레이어 생성
- `PUT /api/lol/players/{id}` - 플레이어 정보 수정
- `DELETE /api/lol/players/{id}` - 플레이어 삭제

#### Champions (챔피언)
- `GET /api/lol/champions` - 모든 챔피언 조회
- `GET /api/lol/champions/{id}` - 특정 챔피언 조회
- `GET /api/lol/champions/name/{name}` - 챔피언 이름으로 조회
- `POST /api/lol/champions` - 새 챔피언 생성
- `PUT /api/lol/champions/{id}` - 챔피언 정보 수정
- `DELETE /api/lol/champions/{id}` - 챔피언 삭제

#### Teams (팀)
- `GET /api/lol/teams` - 모든 팀 조회
- `GET /api/lol/teams/{id}` - 특정 팀 조회
- `POST /api/lol/teams/create` - 자동 밸런스 팀 생성
- `DELETE /api/lol/teams/{id}` - 팀 삭제
- `DELETE /api/lol/teams` - 모든 팀 삭제

### User Service API

Base URL: `http://localhost:8080/api/users`

- `GET /api/users` - 모든 사용자 조회
- `GET /api/users/{id}` - 특정 사용자 조회
- `GET /api/users/username/{username}` - 사용자명으로 조회
- `POST /api/users` - 새 사용자 생성
- `PUT /api/users/{id}` - 사용자 정보 수정
- `DELETE /api/users/{id}` - 사용자 삭제

## 주요 기능

### LOL Team Builder

1. **플레이어 관리**
   - 소환사명, 실명, 선호 포지션, 실력 레벨 등록
   - 플레이어 목록 조회

2. **챔피언 관리**
   - 챔피언 이름, 포지션, 난이도 등록
   - 챔피언 목록 조회

3. **팀 생성**
   - 플레이어 선택
   - 자동 밸런스 팀 생성 (스킬 레벨 기반)
   - 포지션별 자동 배치
   - 팀 평균 실력 표시

## MSA 아키텍처 특징

### API Gateway
- 모든 클라이언트 요청의 단일 진입점
- 각 마이크로서비스로 요청 라우팅
- CORS 설정 중앙 관리

### 서비스 독립성
- 각 서비스는 독립적인 데이터베이스 사용
- 독립적인 배포 가능
- 서비스별 스케일링 가능

### 포트 구성
- API Gateway: 8080
- LOL Team Builder Service: 8081
- User Service: 8082
- LOL DB: 3306
- User DB: 3307
- Frontend: 5173

## 개발 설정

### 데이터베이스 접속 정보

#### LOL Team Builder DB
- Host: localhost
- Port: 3306
- Database: lol_team_builder
- Username: happyhouse
- Password: happyhouse123

#### User Service DB
- Host: localhost
- Port: 3307
- Database: user_service
- Username: happyhouse
- Password: happyhouse123

## Docker Compose 명령어

```bash
# 컨테이너 시작
docker-compose up -d

# 컨테이너 중지
docker-compose down

# 컨테이너 및 볼륨 삭제 (데이터 초기화)
docker-compose down -v

# 로그 확인
docker-compose logs -f
docker-compose logs -f lol-db
docker-compose logs -f user-db
```

## 트러블슈팅

### 포트 충돌

해당 포트들이 이미 사용 중이라면 각 서비스의 `application.yml`에서 포트를 변경하세요:
- API Gateway: 8080
- LOL Team Builder: 8081
- User Service: 8082
- LOL DB: 3306
- User DB: 3307
- Frontend: 5173

### 데이터베이스 연결 오류

1. Docker 컨테이너가 실행 중인지 확인: `docker ps`
2. 데이터베이스 연결 정보 확인: 각 서비스의 `application.yml`
3. 방화벽 설정 확인

### 서비스 간 통신 오류

1. API Gateway가 먼저 실행되었는지 확인
2. 각 마이크로서비스가 정상적으로 실행되었는지 확인
3. API Gateway의 라우팅 설정 확인

## 향후 개선 사항

- [ ] Service Discovery (Eureka) 추가
- [ ] 분산 추적 (Zipkin/Sleuth) 구현
- [ ] Circuit Breaker (Resilience4j) 적용
- [ ] 중앙 집중식 설정 관리 (Spring Cloud Config)
- [ ] 인증/인가 서비스 추가
- [ ] 컨테이너화 (Docker)
- [ ] Kubernetes 배포

## 라이선스

이 프로젝트는 학습 및 개발 목적으로 만들어졌습니다.

## 기여

이슈 및 풀 리퀘스트는 언제든지 환영합니다!
