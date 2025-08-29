# 🌍 TravelMeet - 여행자 매칭 플랫폼

여행 일정을 공유하고 비슷한 시간과 장소에 여행하는 사람들과 만날 수 있는 소셜 매칭 플랫폼입니다.

## 📖 프로젝트 소개

TravelMeet은 **관광 + 소셜 매칭**을 결합한 웹 애플리케이션으로, 사용자가 미리 등록한 여행 일정을 바탕으로 비슷한 시간과 장소에 여행하는 다른 사용자들과 매칭해주는 서비스입니다.

혼자 여행하는 것이 외롭거나, 현지에서 새로운 사람들과 만나고 싶은 여행자들을 위한 플랫폼입니다.

## ✨ 주요 기능

### 🗓️ 여행 일정 등록
- 여행 날짜, 시간, 장소 정보 등록
- 지도 API 연동을 통한 위치 기반 정보 입력
- 여행 스타일 및 선호도 설정

### 🤝 스마트 매칭 시스템
- **시간/장소 기반 매칭**: 비슷한 일정의 여행자 자동 매칭
- **태그 기반 매칭**: 관심분야, 성별, 나이, 국적, 음식 취향 등을 고려한 정교한 매칭
- **매칭 점수 시스템**: 호환성을 점수화하여 최적의 매칭 제공

### 👥 모임 및 만남
- 매칭된 사용자들과의 채팅 기능
- 그룹 모임 생성 및 참여
- 여행 동반자 찾기

### 🔐 안전한 서비스
- 이메일 인증을 통한 회원가입
- Spring Security 기반 보안 시스템
- 신뢰할 수 있는 매칭 환경 제공

## 🛠️ 기술 스택

### Backend
- **Framework**: Spring Boot 3.4.7
- **Language**: Java 21
- **Database**: MySQL 8.0
- **ORM**: JPA/Hibernate + MyBatis
- **Security**: Spring Security 6
- **Mail**: Spring Mail (Gmail SMTP)

### Frontend
- **Template Engine**: Thymeleaf
- **Layout**: Thymeleaf Layout Dialect
- **Styling**: CSS/JavaScript

### Infrastructure
- **Build Tool**: Gradle
- **Development**: Spring Boot DevTools
- **File Upload**: Multipart 지원 (최대 5MB)

## 🚀 시작하기

### 사전 요구사항
- Java 21
- MySQL 8.0
- Gradle

### 설치 및 실행

1. **저장소 클론**
```bash
git clone [repository-url]
cd travel-meet
```

2. **데이터베이스 설정**
```sql
CREATE DATABASE jsl22;
CREATE USER 'jsl22'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON jsl22.* TO 'jsl22'@'localhost';
```

3. **설정 파일 수정**
`application.properties`에서 다음 설정을 본인 환경에 맞게 수정:
- 데이터베이스 연결 정보
- 이메일 설정 (Gmail SMTP)
- 파일 업로드 경로

4. **애플리케이션 실행**
```bash
./gradlew bootRun
```

5. **브라우저에서 접속**
```
http://localhost:8088
```

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/com/shop/
│   │   ├── domain/          # 엔티티 클래스
│   │   ├── controller/      # 컨트롤러
│   │   ├── service/         # 서비스 레이어
│   │   ├── repository/      # 데이터 접근 레이어
│   │   └── config/          # 설정 클래스
│   ├── resources/
│   │   ├── mapper/          # MyBatis 매퍼 XML
│   │   ├── templates/       # Thymeleaf 템플릿
│   │   └── static/          # 정적 리소스
│   └── ...
```

## 🔧 주요 설정

### 데이터베이스
- **MySQL 8.0** 사용
- **JPA/Hibernate**로 엔티티 관리
- **MyBatis**로 복잡한 쿼리 처리

### 보안
- **Spring Security**로 인증/인가 처리
- 이메일 인증 기반 회원가입

### 파일 업로드
- 최대 파일 크기: 5MB
- 최대 요청 크기: 20MB
- 업로드 경로: `D:/upload/item`

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 있습니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 문의사항

프로젝트에 대한 질문이나 제안사항이 있으시면 이슈를 등록해 주세요.

---

**TravelMeet**과 함께 새로운 여행 경험을 만들어보세요! ✈️