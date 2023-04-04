# BaseProject
스프링 부트 베이스 프로젝트입니다. 아래 내용을 구현합니다.
1. 스웨거 구현
2. 테스트 코드 작성
3. 회원 엔티티 구현
4. ~~JWT~~ Spring Session, Spring Security 를 통한 로그인 API 외 인증 절차
5. 이메일 / 비밀번호로 로그인 관리

## 목차
1. [환경구성](#1-환경구성)
2. [API 문서](#2-API-문서)
3. [테스트 코드](#3-테스트-코드)
4. [입력값 검증](#4-입력값-검증)
5. [회원 엔티티 구현](#5-회원-엔티티-구현)
6. [Spring Session, Spring Security 구현](#6-spring-session-spring-security-구현)
7. [로그인 구현](#7-로그인-구현)

## 기타
98. [자체구현](#98-자체구현)
    1. [API 다중호출방어로직](#1-api-다중호출방어로직)
99. [참고](#99-참고)
    1. [커밋 컨벤션](#1-커밋-컨벤션)
    2. [브랜치 전략 - Github flow](#2-브랜치-전략---github-flow)
---
## 1. 환경구성
- Spring Boot 2.7.0 / Gradle 7.4
- Jdk 11
- JUnit5
- Spring Data JPA(with. h2-MySQL8)
- Thymeleaf (with. bootstrap AdminLTE3)

## 2. API 문서
- API 문서를 코드로 관리하기 위해 swagger 를 적용하였습니다.
- swagger-ui 3.0 / springdocs 디펜던시로 구현
- 추가 설정없이 사용가능한 sprigdocs 로 swagger3 구현함.
  ```text
  Spring Boot 2.6 이후 ControllerHandler 매핑 전략 기본값이 아래와 같이 변경됨에 따라
  springfox 디펜던시 기본 설정으로는 사용 불가

  ant_path_matcher -> path_pattern_parser
  ```

## 3. 테스트 코드
- Spring Boot 2.7.0 기준 spring-starter-test 에 JUnit5 가 내장되어 있어 JUnit5를 사용합니다.
- io.spring.dependency-management 에 의해 스프링 관련 디펜던시가 일괄 관리되기 때문에 위 디펜던시 버전은 변경하지 않습니다.
- [JUnit4 -> JUnit5 메소드 변경점 참고 블로그](https://theheydaze.tistory.com/218)

## 4. 입력값 검증
1. 컨트롤러 진입 전 Dto 검증
2. 서비스코드 진입 전 Dto 검증
* [Dto, Entity 검증 정리한 블로그](https://velog.io/@idean3885/Dto-Entity-Validation-%EC%B2%98%EB%A6%AC#2-entity-validation---validated)

## 5. 회원 엔티티 구현
- Spring Data JPA(with. H2-MySQL8) 로 작성
- 기능 구현 중심의 프로젝트이기 때문에 H2 데이터베이스의 인메모리 방식을 사용합니다.
- 공공데이터 포털에서 관리되는 표준용어 기준으로 ERD를 작성하고 구현했습니다.
  + ~~[(폐쇄됌.)공공데이터 공통표준용어 폐쇄된 페이지 확인](https://data.seoul.go.kr/commonList/commonList.do)~~
- [작성된 ERD 확인하기(ERDCloud)](https://www.erdcloud.com/d/ZG8wGTXTmkTyL8qdp)

## 6. Spring Session, Spring Security 구현
- TBD

## 7. 로그인 구현
- 이메일, 패스워드로 구현
- TBD

## 98. 자체구현
### 1. API 다중호출방어로직
#### 목적
- 동일 사용자(세션동일)가 같은 API를 다중호출하는 경우를 방지함.
- 처리완료된 API를 다시 호출하는 경우, 유효한 세션이므로 비즈니스 로직이 수행됨.  
  따라서 불필요한 검증로직이 동작하거나, 같은 작업이 두번 수행되어 데이터 정합성이 깨지는 경우가 발생할 수 있다.
  ```text
   예) ABC사용자가 1111 계좌에 1000원을 입금하는 경우
      입금 처리까지 0.5초가 걸린다고 했을 때, 그 사이에 재요청이 온다면 검증 또한 성공한다.
 
  1. 최초 입금 요청 -> 입금 로직 수행 중(외부 통신 등으로 시간 소요 중)
  2. 1번 처리 도중 입금 재요청 -> 1번 처리가 끝날 때까진 입금완료가 아니기 때문에 입금 로직 수행
  3. 1번 처리 완료 -> 1차 입금
  4. 2번 처리 완료 -> 2차 입금 => 중복 입금 발생
  ```
- 이를 해결하기 위해 API 다중호출 방어로직을 구현함

#### 구현방안
- JavaScript 의 Debounce 전략 사용(이하 Debounce 기능)
- 커스텀 어노테이션 @Debounce 를 설정하고 이를 인터셉터에서 필터링하는 방식으로 구현함.
  인터셉터 PreHandle 단계에서 세션 별 동일 API 에 대해 최종호출시간을 비교하여 최초 호출 이후 일정시간동안 호출을 차단한다.  
  [Debounce 기능 정리한 블로그](https://velog.io/@idean3885/API-%EB%8B%A4%EC%A4%91-%ED%98%B8%EC%B6%9C-%EC%9D%B4%EC%8A%88-%EC%B2%98%EB%A6%AC)  
  [구현 GitHub 이슈](https://github.com/idean3885/BaseProejct/issues/6)

## 99. 참고
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
    - `이슈번호/커밋 종류: 커밋 내용` -> 50자 내외, 마침표 없이 작성
    - 부족할 경우, 본문에 상세한 내용 작성
    - `이슈 단위로 작업하며, 이슈에 커밋 히스토리를 연결하기 위해 이슈 번호를 기재한다.`
3. 본문(선택)
    - 제목에서 두 줄 띄고 작성 -> 깃에서 제목과 본문을 구분하는 방식
    - 한 줄당 72자 내로 작성
    - 최대한 상세하게 작성할 것
4. 예시
   ```
   #6/feat: 회원 테이블 구현

   JPA 로 구현함
   컬럼 - id, email, password, name
   컬럼은 필요최소한으로 구현했으며, 추후 변경될 여지가 있음.
   ```

### 2. 브랜치 전략 - GitHub Flow
- 이슈 단위의 간결한 코드 관리를 위해 GitHub Flow 로 선택함
- 명명규칙 `feature/#issue_number-issue-title`
- ***이슈 처리 과정에서 커밋되는 코드들을 관리하기 위함이기 때문에 반드시 이슈 발행 후 브랜치 작성할 것.***

