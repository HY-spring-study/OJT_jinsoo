# 📌 [OJT] 게시판 프로젝트

> Spring Boot, JPA, Thymeleaf를 이용한 간단한 게시판.

## 📖 개요
이 프로젝트는 웹 기반 게시판 애플리케이션으로, 게시글이나 댓글의 CRUD기능을 구현할 예정입니다.<br>
백엔드는 Spring Boot를 사용했으며, 프론트엔드는 Thymeleaf와 Bootstrap을 사용합니다(SSR).

## 🔧 기술 스택
- **백엔드:** Spring Boot, Spring MVC, Spring Data JPA
- **데이터베이스:** H2 (개발용), 현재 미정 (운영용)
- **프론트엔드:** Thymeleaf, Bootstrap
- **빌드 도구:** Gradle
- **버전 관리:** Git & GitHub

## 📂 프로젝트 구조 (진행 중)
```
📂 ojtcommunity/
 ├── 📁 src/
 │   ├── 📁 main/
 │   │   ├── 📁 java/parksoffice/ojtcommunity/  # Java 소스 파일
 │   │   │   ├── 📁 config/   # 설정 관련 클래스
 │   │   │   │   └── DataInitializer.java
 │   │   │   ├── 📁 controller/   # 컨트롤러 (웹 요청 처리)
 │   │   │   │   ├── BoardController.java
 │   │   │   │   └── HomeController.java
 │   │   │   ├── 📁 service/   # 비즈니스 로직
 │   │   │   │   └── MemberService.java
 │   │   │   ├── 📁 repository/   # 데이터 접근 계층 (DAO)
 │   │   │   │   ├── 📁 board/   # 게시판 관련 리포지토리
 │   │   │   │   │   ├── BoardRepository.java
 │   │   │   │   │   └── PostRepository.java
 │   │   │   │   ├── 📁 member/   # 회원 관련 리포지토리
 │   │   │   │   │   └── MemberRepository.java
 │   │   │   ├── 📁 domain/   # 엔티티 클래스 (JPA)
 │   │   │   │   ├── 📁 board/   # 게시판 관련 엔티티
 │   │   │   │   │   ├── Board.java
 │   │   │   │   │   └── Post.java
 │   │   │   │   ├── 📁 common/   # 공통 엔티티
 │   │   │   │   │   └── BaseEntity.java
 │   │   │   │   ├── 📁 member/   # 회원 관련 엔티티
 │   │   │   │   │   └── Member.java
 │   │   │   ├── 📁 dto/   # 데이터 전송 객체 (DTO)
 │   │   │   │   └── UpdateMemberDto.java
 │   │   │   ├── 📁 exception/   # 예외 처리 클래스
 │   │   │   │   ├── DuplicateMemberException.java
 │   │   │   │   └── MemberNotFoundException.java
 │   │   │   ├── OjtCommunityApplication.java  # 메인 애플리케이션 클래스
 │   │   ├── 📁 resources/
 │   │   │   ├── 📁 templates/   # Thymeleaf 템플릿 파일
 │   │   │   ├── 📁 static/   # 정적 파일 (CSS, JS, 이미지)
 │   │   │   │   ├── 📁 css/
 │   │   │   │   └── index.html
 │   │   │   ├── application.yml  # 애플리케이션 설정 파일
 ├── 📁 test/   # 테스트 코드
 │   ├── 📁 java/parksoffice/ojtcommunity/  # 테스트 파일
 │   │   ├── OjtCommunityApplicationTests.java
 │   │   ├── 📁 service/
 │   │   │   └── MemberServiceTest.java
 ├── build.gradle   # Gradle 빌드 파일
 ├── settings.gradle  # Gradle 설정 파일
 ├── README.md   # 프로젝트 문서
```

## 📌 주요 기능
- ✅ **게시글 CRUD (등록, 조회, 수정, 삭제)**
- ✅ **댓글 기능**
- ✅ **사용자 인증 (로그인, 회원가입, [선택]JWT 기반 보안)**
- ✅ **RESTful API 제공**
- ✅ **데이터베이스 사용**

## 🛠️ 개발 환경
- **Java** 21
- **Spring Boot** 3.4.2
- **Gradle**
- **H2**
- **IntelliJ IDEA**

## 🔒 보안 및 코드 품질
이 프로젝트에서는 민감 정보가 실수로 커밋되는 것을 방지하기 위해 Gitleaks를 사용하고 있어요.
#### 주요 내용:

- Gitleaks 통합:
  pre-commit 훅에 Gitleaks를 통합하여 커밋 전에 자동으로 민감 정보(예: API 키, 비밀번호 등)를 스캔합니다.

- 목적:
  안전한 코드베이스 유지를 위해 개발 과정에서 민감 정보가 포함되지 않도록 사전 검증을 실시합니다.

## 📜 라이선스
이 프로젝트는 개인 프로젝트이며, 현재 라이선스를 적용하지 않았습니다.

