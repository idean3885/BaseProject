# BaseProject
스프링 부트 베이스 프로젝트입니다. 아래 내용을 구현합니다.
1. 스웨거 구현
2. 테스트 코드 작성
3. 회원 엔티티 구현
4. JWT, Spring Security 를 통한 로그인 API 외 인증 절차
5. 이메일 / 비밀번호로 로그인 관리

## 목차
1. [스웨거](#1-스웨거)
2. [테스트 코드](#2-테스트-코드)
3. [입력값 검증](#3-입력값-검증)
4. [회원 엔티티 구현](#4-회원-엔티티-구현)
5. [JWT, Spring Security 구현](#5-jwt-spring-security-구현)
6. [로그인 구현](#6-로그인-구현)

* [참고](#참고)
   1. [커밋 컨벤션](#1-커밋-컨벤션)
   2. [브랜치 전략](#2-브랜치-전략)
---

## 1. API 문서
- swagger-ui 3.0 / springdocs 디펜던시로 구현
- Spring Boot 2.6 이후 ControllerHandler 매핑 전략 기본값이 ant_path_matcher -> path_pattern_parser 로 변경됨에 따라 springfox 디펜던시 기본 설정으로는 사용 불가
- 추가 설정없이 사용가능한 sprigdocs 로 swagger3 구현함.

## 2. 테스트 코드
- Spring Boot 2.7.0 기준 spring-starter-test 에 JUnit5 가 내장되어 있어 JUnit5를 사용합니다.
- io.spring.dependency-management 에 의해 스프링 관련 디펜던시가 일괄 관리되기 때문에 위 디펜던시 버전은 변경하지 않습니다.
- [JUnit4 -> JUnit5 메소드 변경점 참고 블로그](https://theheydaze.tistory.com/218)
- TBD
- 
## 3. 입력값 검증
- 컨트롤러 진입 전 Dto 검증
- 서비스코드 진입 전 Dto 검증
- 상세내용 TBD

## 4. 회원 엔티티 구현
- JPA 예정
- H2-PostgreSQL 예정
- 기능 구현 중심의 프로젝트이기 때문에 H2 데이터베이스의 인메모리 방식을 사용합니다.
- jdbc 드라이버는 PostgreSQl 을 사용합니다.   
  Heroku 에서 PostgreSQL 10000 row 까지 무료로 제공되고 있어 토이 프로젝트 진행 시 데이터베이스 코드를 바로 사용할 수 있어 선택했습니다.
  
## 5. JWT, Spring Security 구현
- TBD

## 6. 로그인 구현
- 이메일, 패스워드로 구현
- TBD

## 참고
### 1. 커밋 컨벤션
1. 커밋 종류   
   |type|desc|
   |--|--|
   |feat|새로운 기능 추가|
   |fix|버그 수정|
   |docs|문서 수정|
   |style|코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우|
   |refactor|코드 리팩토링|
   |test|테스트 코드, 리팩토링 테스트 코드 추가|
   |chore|빌드 업무 수정, 패키지 매니저 수정|
2. 제목(필수)
     - `커밋 종류: 커밋 내용` -> 50자 미만, 마침표 없이 작성
     - 부족할 경우, 본문에 상세한 내용 작성
3. 본문(선택)
     - 제목에서 두 줄 띄고 작성 -> 깃에서 제목과 본문을 구분하는 방식
     - 한 줄당 72자 내로 작성
     - 최대한 상세하게 작성할 것
4. 예시
   ```
   feat: 회원 테이블 구현

   JPA 로 구현함
   컬럼 - id, email, password, name
   컬럼은 필요최소한으로 구현했으며, 추후 변경될 여지가 있음.
   ```

### 2. 브랜치 전략
1. GitHub Flow
     - 명명규칙 `feature/#issue_number-issue-title`
     - 이슈 단위의 간결한 코드 관리를 위해 GitHub Flow 로 선택함
     - ***이슈 처리 과정에서 커밋되는 코드들을 관리하기 위함이기 때문에 반드시 이슈 발행 후 브랜치 작성할 것.***

