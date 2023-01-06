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
99. [방어로직](#99-방어로직)

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

## 3. 입력값 검증
1. 컨트롤러 진입 전 Dto 검증
2. 서비스코드 진입 전 Dto 검증   
* [Dto, Entity 검증 정리한 블로그](https://velog.io/@idean3885/Dto-Entity-Validation-%EC%B2%98%EB%A6%AC#2-entity-validation---validated)

## 4. 회원 엔티티 구현
- JPA 예정
- H2-PostgreSQL 예정
- 기능 구현 중심의 프로젝트이기 때문에 H2 데이터베이스의 인메모리 방식을 사용합니다.
- ~~jdbc 드라이버는 PostgreSQl 을 사용합니다.~~   
  ~~Heroku 에서 PostgreSQL 10000 row 까지 무료로 제공되고 있어 토이 프로젝트 진행 시 데이터베이스 코드를 바로 사용할 수 있어 선택했습니다.~~  
  프리티어가 없어짐에 따라 jdbc 를 익숙한 MySQL 로 변경하고 우선 H2 인메모리로 구현진행
  
## 5. JWT, Spring Security 구현
- TBD

## 6. 로그인 구현
- 이메일, 패스워드로 구현
- TBD

## 99. 방어로직
### 1. API 다중호출방지
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

