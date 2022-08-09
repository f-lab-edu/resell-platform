# **👟리셀 플랫폼**
- Kream 리셀 플랫폼을 클론 구현한 프로젝트
- 백엔드 설계에 집중하기 위해 프론트 UI는 카카오 오븐을 이용한 프로토 타이핑으로 대체함. 프로토 타입 UI와 통신하는 백엔드 API 개발에 주력.

<br>

# **🧑‍💻사용 기술 및 개발 환경**
**협업 툴**
- 형상 관리: Github
- 이슈 관리: Github Issue
- 진행 관리: Github Project
- [결정 과정 ](https://github.com/f-lab-edu/resell-platform/wiki/협업-툴-및-규칙-결정-과정)

<br>

# **🌐아키텍처**
![Back-end-CI CD (4)](https://user-images.githubusercontent.com/50356726/183281514-3700f7c4-94f3-45d7-a996-17b9a535f35e.png)

<br>

# **🔧사용 기술**
## Backend

- Java11
- Spring Boot 2.7.0, Spring MVC, Spring Security
- Mybatis
- Junit5, Mockito
- Gradle 7.x

## DevOps

- Ubuntu 20.04
- NaverCloud
- MySQL
- Github Action

## Collaboration & Tools

- Slack, Notion
- IntelliJ
- Git, Github Project

<br>

# **❓기술적 이슈**
## 기술 선택 과정

🔐[사용자의 UX를 고려한 로그인(JWT, 세션, 쿠키) 보안 전략 수립 및 구현](https://ujkim-game.tistory.com/74)

🤐[브루트포스 공격 특성을 고려한 비밀번호 해싱 알고리즘 선정 및 Spring 적용](https://ujkim-game.tistory.com/67)

🗳️[[CI,CD] 젠킨스, Github Action 비교](https://www.notion.so/CI-CD-Github-Action-48925eb72a75429a99edcfcbc3534497)

📕[[ms] Session storage selection Redis vs Memcached](https://www.notion.so/ms-Session-storage-selection-Redis-vs-Memcached-33960639090b4096984dd9be6e287267)

## 해결한 이슈

🗒️[유스케이스 기반 통합테스트 작성](https://www.notion.so/cbc35a5e03db4b8dbd7cb3c102f4be32)

📌[docker-compose 파일 내 중복 ****Compose configurations**** 존재(1)](https://www.notion.so/docker-compose-Compose-configurations-1-592768983c7d45b1884f068648c9f13b)

📌[docker-compose 파일 내 중복 ****Compose configurations**** 존재(2)](https://www.notion.so/docker-compose-Compose-configurations-2-718321adbbf34b6886287fc724e64678)

🐳도커와 Testcontainer를 활용한 서버 환경 구축 자동화

🪟[Filter를 사용하여 반복되는 응답 로직 제거](https://ujkim-game.tistory.com/72)

## Small 이슈

🥜Prototype Bean과 MockBean을 이용한 ThreadLocal의 단위 테스트 구축

⚠️@Transactional과 @Cacheable 호환 문제 해결


<br>

# **🔍유스케이스**
- 프로그래머 2인 개발이기 때문에 기획직군으로부터 받는 상세 기획서가 존재하지 않았습니다.
- 기능 개발 및 통합 테스트 시나리오 작성의 용이함을 위해 Cockburn style의 유스케이스를 작성했습니다.
[자세한 구현 내용 링크](https://github.com/f-lab-edu/resell-platform/wiki/유스케이스)

### **대표 예시**
<html>
<body>
<!--StartFragment-->

속성 | 내용
-- | --
유스케이스 | **비밀번호 변경**
액터 | 유저 권한 고객
범위 | 유저 서비스
수준 | 사용자 목표(중간 수준)
주요 성공 시나리오 |  
  | 1. 고객은 로그인을 요청하여 유저 서비스로부터 유저 권한을 획득한다.
  | 2. 유저 권한 고객은 유저 서비스에게 비밀번호 변경을 요청한다.
실패 시나리오 |  
  | 1. 유저 권한이 없는 고객은 유저 서비스에게 비밀번호 변경을 요청할 수 없다.


<!--EndFragment-->
</body>
</html>


<br>

# **📲애플리케이션 UI**
- **프로토 타이핑 툴:** 카카오 오븐
- **구현 목적:** 기능 정의 래퍼런스

[자세한 구현 내용 및 웹 테스트 링크](https://github.com/f-lab-edu/resell-platform/wiki/Application-UI)

### **대표 예시**
![Untitled Diagram drawio](https://user-images.githubusercontent.com/50356726/183283422-5363c8f9-8154-406e-87ba-c332f2279138.png)

<br>

# **📄ERD**
- **설계 래퍼런스**: [애플리케이션 UI](https://user-images.githubusercontent.com/50356726/172335644-ab179281-5d27-4718-bf0b-91cfa01ab470.png)
- **ERD 설계 툴**: MySQL Workbench

  
[자세한 구현 내용 및 사고 과정 ](https://github.com/f-lab-edu/resell-platform/wiki/ERD)


![Untitled Diagram (3)](https://user-images.githubusercontent.com/50356726/183283583-754f6426-a732-4823-b1e1-e6fddcfb9200.jpg)

