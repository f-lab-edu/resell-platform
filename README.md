# **👟리셀 플랫폼**
![image](https://user-images.githubusercontent.com/50356726/183287083-90d7706f-1772-4e0a-9e35-00cbf194c492.png)

<br>

# 🤝소개
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
![Back-end-CI CD_0905](https://user-images.githubusercontent.com/50356726/188408969-3eb95d7c-1daa-4f92-8ae0-1a6a1394bfb8.jpg)

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
- AWS EC2, CodeDeploy, S3, SecretsManger

## Collaboration & Tools

- Slack, Notion
- IntelliJ
- Git, Github Project

<br>

# **❓기술적 이슈**
- 개발 시에 겪었던 **기술적 Issue들을 문제 상황, 접근 방법과 해결 과정, 결과**로 작성하려고 노력했습니다.
- 그 중에서도 왜 어떠한 기술을 선택하게 됐는지 **사고 과정**을 중점적으로 작성했습니다.
- [기술 선택 과정 링크](https://github.com/f-lab-edu/resell-platform/wiki/기술적-Issue#기술-선택-과정)
- [해결한 이슈 링크](https://github.com/f-lab-edu/resell-platform/wiki/기술적-Issue#해결한-이슈)
- [Small 이슈 링크](https://github.com/f-lab-edu/resell-platform/wiki/기술적-Issue#small-이슈)


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

