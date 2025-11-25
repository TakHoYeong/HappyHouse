# HappyHouse

React와 Spring Boot를 사용한 풀스택 웹 애플리케이션입니다.

## 기술 스택

### 백엔드
- **Java**: 21
- **Spring Boot**: 3.5.7
- **Spring Data JPA**
- **Hibernate**
- **MariaDB**: 11.2
- **Lombok**
- **Gradle**

### 프론트엔드
- **React**: 18
- **Vite**: 최신 버전
- **Axios**: HTTP 클라이언트

### 인프라
- **Docker & Docker Compose**: 데이터베이스 컨테이너 관리

## 프로젝트 구조

```
HappyHouse/
├── src/
│   ├── main/
│   │   ├── java/com/tacoringo/happyhouse/
│   │   │   ├── domain/          # 엔티티 클래스
│   │   │   ├── repository/      # JPA Repository
│   │   │   ├── service/         # 비즈니스 로직
│   │   │   └── controller/      # REST API 컨트롤러
│   │   └── resources/
│   │       └── application.yml  # Spring Boot 설정
│   └── test/
├── frontend/                     # React 프로젝트
│   ├── src/
│   ├── package.json
│   └── vite.config.js
├── docker-compose.yml
├── build.gradle
└── README.md
```

## 시작하기

### 사전 요구사항

- **Java 21** 설치
- **Node.js** 22.x 이상
- **Docker & Docker Compose** 설치
- **Gradle** (또는 내장된 Gradle Wrapper 사용)

### 1. 데이터베이스 실행

Docker Compose를 사용하여 MariaDB를 실행합니다:

```bash
docker-compose up -d
```

MariaDB 접속 정보:
- **Host**: localhost
- **Port**: 3306
- **Database**: happyhouse
- **Username**: happyhouse
- **Password**: happyhouse123
- **Root Password**: root123

### 2. 백엔드 실행

```bash
# Gradle Wrapper를 사용한 빌드 및 실행
./gradlew bootRun

# 또는 빌드 후 JAR 실행
./gradlew build
java -jar build/libs/happyhouse-0.0.1-SNAPSHOT.jar
```

백엔드 서버는 `http://localhost:8080`에서 실행됩니다.

### 3. 프론트엔드 실행

```bash
cd frontend
npm install  # 처음 한 번만 실행
npm run dev
```

프론트엔드 개발 서버는 `http://localhost:3000`에서 실행됩니다.

## API 엔드포인트

### User API

Base URL: `http://localhost:8080/api/users`

- `GET /api/users` - 모든 사용자 조회
- `GET /api/users/{id}` - 특정 사용자 조회
- `GET /api/users/username/{username}` - 사용자명으로 조회
- `POST /api/users` - 새 사용자 생성
- `PUT /api/users/{id}` - 사용자 정보 수정
- `DELETE /api/users/{id}` - 사용자 삭제

### 요청 예시

```bash
# 사용자 생성
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "name": "홍길동",
    "phone": "010-1234-5678"
  }'

# 모든 사용자 조회
curl http://localhost:8080/api/users
```

## 개발 설정

### application.yml 주요 설정

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/happyhouse
    username: happyhouse
    password: happyhouse123

  jpa:
    hibernate:
      ddl-auto: update  # 개발: update, 운영: validate
    show-sql: true
```

### Vite 프록시 설정

프론트엔드에서 백엔드 API를 호출할 때 CORS 문제를 피하기 위해 Vite 프록시가 설정되어 있습니다:

```javascript
// vite.config.js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    }
  }
}
```

## 데이터베이스 관리

### Docker Compose 명령어

```bash
# 컨테이너 시작
docker-compose up -d

# 컨테이너 중지
docker-compose down

# 컨테이너 및 볼륨 삭제 (데이터 초기화)
docker-compose down -v

# 로그 확인
docker-compose logs -f mariadb
```

### MariaDB 접속

```bash
# Docker 컨테이너를 통한 접속
docker exec -it happyhouse-mariadb mysql -u happyhouse -p
# Password: happyhouse123
```

## 빌드 및 배포

### 백엔드 빌드

```bash
./gradlew clean build
```

빌드된 JAR 파일: `build/libs/happyhouse-0.0.1-SNAPSHOT.jar`

### 프론트엔드 빌드

```bash
cd frontend
npm run build
```

빌드된 파일: `frontend/dist/`

## 트러블슈팅

### 포트 충돌

- 백엔드: 8080 포트 사용
- 프론트엔드: 3000 포트 사용
- MariaDB: 3306 포트 사용

해당 포트들이 이미 사용 중이라면 설정 파일에서 포트를 변경하세요.

### 데이터베이스 연결 오류

1. Docker 컨테이너가 실행 중인지 확인: `docker ps`
2. 데이터베이스 연결 정보 확인: `application.yml`
3. 방화벽 설정 확인

## 라이선스

이 프로젝트는 학습 및 개발 목적으로 만들어졌습니다.

## 기여

이슈 및 풀 리퀘스트는 언제든지 환영합니다!
