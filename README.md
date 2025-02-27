# 📌 [OJT] 게시판 프로젝트

> Spring Boot, JPA, Thymeleaf를 이용한 간단한 게시판.

## 📖 개요
이 프로젝트는 웹 기반 게시판 애플리케이션으로, 게시글의 CRUD 와 추천 기능 및 사용자 인증(로그인, 회원가입)을 구현합니다.  
백엔드는 Spring Boot와 Spring Data JPA를 사용하고, 프론트엔드는 Thymeleaf와 Bootstrap을 활용하여 서버 사이드 렌더링(SSR) 방식으로 구축되었습니다

## 🔧 기술 스택
- **백엔드:** Spring Boot, Spring MVC, Spring Data JPA
- **데이터베이스:** H2 (개발용), 운영용 DB는 추후 결정
- **프론트엔드:** Thymeleaf, Thymeleaf Layout Dialect, Bootstrap 5.3.3
- **빌드 도구:** Gradle
- **버전 관리:** Git & GitHub

## 📂 프로젝트 구조
```
📂 ojtcommunity/
 ├── 📁 src/
 │   ├── 📁 main/
 │   │   ├── 📁 java/parksoffice/ojtcommunity/  
 │   │   │   ├── 📁 config/         # 설정 관련 클래스 (DataInitializer 등)
 │   │   │   ├── 📁 controller/     # 컨트롤러 (BoardController, HomeController, MemberController)
 │   │   │   ├── 📁 domain/         # 엔티티 클래스
 │   │   │   │   ├── 📁 board/      # 게시판 관련 엔티티 (Board, Post, PostRecommendation)
 │   │   │   │   ├── 📁 common/     # 공통 엔티티 (BaseEntity)
 │   │   │   │   ├── 📁 member/     # 회원 관련 엔티티 (Member)
 │   │   │   ├── 📁 dto/            # 데이터 전송 객체 (CreatePostDto, LoginRequestDto 등)
 │   │   │   ├── 📁 exception/      # 예외 처리 클래스 (MemberNotFoundException 등)
 │   │   │   ├── 📁 repository/     # 데이터 접근 계층 (BoardRepository, PostRepository, MemberRepository)
 │   │   │   ├── 📁 service/        # 비즈니스 로직 (BoardService, MemberService, PostService)
 │   │   │   └── OjtCommunityApplication.java  # 메인 애플리케이션 클래스
 │   │   ├── 📁 resources/
 │   │   │   ├── 📁 templates/       # Thymeleaf 템플릿 파일
 │   │   │   │   ├── 📁 board/       # 게시판 관련 뷰 (lists.html, view.html 등)
 │   │   │   │   ├── 📁 members/     # 회원 관련 뷰 (login.html, create.html)
 │   │   │   │   ├── 📁 fragments/   # 공통 레이아웃 프래그먼트 (head.html, header.html, footer.html)
 │   │   │   │   ├── layout.html    # 전체 레이아웃 템플릿
 │   │   │   │   ├── index.html     # 메인 페이지
 │   │   │   │   ├── error.html     # 에러 페이지
 │   │   │   ├── 📁 static/         # 정적 파일 (CSS, JS, 이미지)
 │   │   │   │   └── 📁 css/        # 공통 스타일 파일
 │   │   │   ├── application.yml   # 애플리케이션 설정 파일
 ├── 📁 test/                       # 테스트 코드
 │   ├── 📁 java/parksoffice/ojtcommunity/
 │   │   ├── OjtCommunityApplicationTests.java
 │   │   ├── 📁 service/            # 서비스 테스트 코드
 │   │   │   ├── MemberServiceTest.java
 │   │   │   └── PostServiceTest.java
 ├── build.gradle                   # Gradle 빌드 파일
 ├── settings.gradle                # Gradle 설정 파일
 └── README.md                      # 프로젝트 문서
 ```

## 📌 주요 기능
- ✅ **게시글 CRUD (등록, 조회, 수정, 삭제)**
- ✅ **회원 기능 (회원가입, 로그인, 로그아웃)**
- ✅ **추천 기능 (각 회원은 한 게시글에 대해 한 번만 추천 가능)**
- ✅ **Thymeleaf 레이아웃 (공통 레이아웃을 분리하여 재사용성 향상)**
- ✅ **예외 처리 (사용자 친화적인 예외 응답 및 에러 페이지 제공)**

## 🛠️ 개발 환경
- **Java** 21
- **Spring Boot** 3.4.2
- **Gradle**
- **H2**
- **IntelliJ IDEA**

## 🔧 추가 설정 및 모듈화

### 🛠 Thymeleaf Layout Dialect 설정
`build.gradle`에 아래 의존성을 추가하여 **공통 레이아웃을 쉽게 관리**할 수 있습니다.

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.thymeleaf.extras:thymeleaf-layout-dialect'
    // 기타 의존성...
}
```

## 🔒 보안 및 코드 품질
이 프로젝트에서는 민감 정보가 실수로 커밋되는 것을 방지하기 위해 Gitleaks를 사용하고 있어요.
#### 주요 내용:

- Gitleaks 통합:
  pre-commit 훅에 Gitleaks를 통합하여 커밋 전에 자동으로 민감 정보(예: API 키, 비밀번호 등)를 스캔합니다.

- 목적:
  안전한 코드베이스 유지를 위해 개발 과정에서 민감 정보가 포함되지 않도록 사전 검증을 실시합니다.

## 📜 라이선스
이 프로젝트는 개인 프로젝트이며, 현재 라이선스를 적용하지 않았습니다.

