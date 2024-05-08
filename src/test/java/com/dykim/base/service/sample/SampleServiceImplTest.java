package com.dykim.base.service.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dykim.base.advice.sample.exception.SampleAlreadyExistException;
import com.dykim.base.advice.sample.exception.SampleNotFoundException;
import com.dykim.base.dto.sample.SampleInsertReqDto;
import com.dykim.base.dto.sample.SampleUpdateReqDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 *
 * <h3>HelloServiceTest 코드</h3>
 *
 * 테스트 프로파일로 실제 구동하여 서비스 로직을 점검한다.<br>
 * H2 데이터베이스 및 jpa create-drop 옵션으로 서버 구동마다 데이터베이스를 새로 생성한다.<br>
 * <br>
 * <b>참고) DML 기능 테스트 시 주의사항</b>
 *
 * <pre>
 * 실 서버 구동이기 때문에 메소드가 종료되어도 데이터가 남아있어
 * 다른 테스트 케이스에 영향이 생길 수 있다.
 * 이를 해결하기 위해 @Transaction 을 메소드 단위로
 * 선언하여 테스트 성공 시 롤백처리하도록 한다.
 *
 * 테스트용 데이터베이스를 H2 인메모리 DB에 매번 새로 생성하기 때문에
 * 실패하여 롤백되지 않는 케이스는 고려하지 않는다.
 *
 * 테스트 메소드(@Test 선언된 메소드) 의 경우, JpaTransactionManager 의
 * rollback 옵션 기본값이 true 이기 때문에 특별한 설정없이 롤백처리할 수 있다.
 * </pre>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("internal")
@SpringBootTest
class SampleServiceImplTest {

    private final SampleServiceImpl sampleServiceImpl;

    @Autowired
    public SampleServiceImplTest(SampleServiceImpl sampleServiceImpl) {
        this.sampleServiceImpl = sampleServiceImpl;
    }

    @Order(1)
    @Test
    @Transactional
    void insert_return_HelloInsertRspDto() {
        // given
        var reqDto =
                SampleInsertReqDto.builder()
                        .email("success@email.com")
                        .name("name")
                        .birthday(parseBirthday("1900-01-01"))
                        .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                        .build();

        // when
        var rspDto = sampleServiceImpl.insert(reqDto);

        // then
        assertThat(rspDto.getEmail()).isEqualTo(reqDto.getEmail());
        assertThat(rspDto.getName()).isEqualTo(reqDto.getName());
        assertThat(rspDto.getBirthday()).isEqualTo(reqDto.getBirthday());
    }

    @Order(2)
    @Test
    void insert_with_empty_email_throw_ConstraintViolationException() {
        // given
        var reqDto =
                SampleInsertReqDto.builder()
                        .name("name")
                        .birthday(parseBirthday("2001-01-01"))
                        .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                        .build();

        // when-then
        assertThrows(ConstraintViolationException.class, () -> sampleServiceImpl.insert(reqDto));
    }

    @Order(3)
    @Test
    void insert_invalid_email_format_throw_ConstraintViolationException() {
        // given
        var reqDto =
                SampleInsertReqDto.builder()
                        .email("email.com")
                        .name("name")
                        .birthday(parseBirthday("2001-01-01"))
                        .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                        .build();

        // when-then
        assertThrows(ConstraintViolationException.class, () -> sampleServiceImpl.insert(reqDto));
    }

    @Order(4)
    @Test
    void insert_with_empty_name_throw_ConstraintViolationException() {
        // given
        var reqDto =
                SampleInsertReqDto.builder()
                        .email("email.com")
                        .birthday(parseBirthday("2001-01-01"))
                        .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                        .build();

        // when-then
        assertThrows(ConstraintViolationException.class, () -> sampleServiceImpl.insert(reqDto));
    }

    @Order(5)
    @Test
    void insert_with_duplicate_email_throw_HelloAlreadyExistException() {
        // setup
        var mockHelloInsertRspDto =
                sampleServiceImpl.insert(
                        SampleInsertReqDto.builder()
                                .email("duple@email.com")
                                .name("mock1")
                                .birthday(parseBirthday("1993-07-24"))
                                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                                .build());

        // given
        var reqDto =
                SampleInsertReqDto.builder()
                        .email(mockHelloInsertRspDto.getEmail())
                        .name("name")
                        .birthday(parseBirthday("1900-01-01"))
                        .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                        .build();

        // when-then
        assertThrows(SampleAlreadyExistException.class, () -> sampleServiceImpl.insert(reqDto));
    }

    @Order(6)
    @Test
    @Transactional
    void find_by_id_return_HelloFindRspDto() {
        // setup
        var mockSampleInsertRspDto =
                sampleServiceImpl.insert(
                        SampleInsertReqDto.builder()
                                .email("mock1@email.com")
                                .name("mock1")
                                .birthday(parseBirthday("1993-07-24"))
                                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                                .build());

        // given
        var sampleId = mockSampleInsertRspDto.getId();

        // when
        var rspDto = sampleServiceImpl.select(sampleId);

        // then
        assertThat(rspDto.getName()).isEqualTo(mockSampleInsertRspDto.getName());
        assertThat(rspDto.getEmail()).isEqualTo(mockSampleInsertRspDto.getEmail());
        assertThat(rspDto.getBirthday()).isEqualTo(mockSampleInsertRspDto.getBirthday());
        assertThat(rspDto.getYyyyMMddHHmmssSSS())
                .isEqualTo(mockSampleInsertRspDto.getYyyyMMddHHmmssSSS());
    }

    /**
     *
     *
     * <h3>JPA null find 예외 테스트</h3>
     *
     * <pre>
     * id 에 null 이 입력되는 케이스를 테스트한다.
     * 실제 컨트롤러를 통해 진입 시, PathVariable 로 입력받고
     * 자료형 검증도 이루어지기 때문에 발생할 가능성은 없지만 샘플 케이스로서 작성함.
     * </pre>
     *
     * <b>참고</b>
     *
     * <pre>
     * JPA Repository.find(null) 실행 시 명세와 달리
     * InvalidDataAccessApiUsageException 이 발생한다.
     *
     * 이는 EntityManagerFactoryUtils 이 내부적으로
     * IllegalArgumentException 을 변경시키기 때문이다.
     *
     * 스프링 내에서 JPA 는 AOP 로 동작하게 되며 JdkDynamicAopProxy 로
     * 실행하는 과정에서 위 오류 발생 시 예외를 변경시켜주고 있다.
     * </pre>
     */
    @Order(7)
    @Test
    void find_with_null_id_throw_InvalidDataAccessApiUsageException() {
        // given
        Long sampleId = null;

        // when-then
        assertThrows(
                InvalidDataAccessApiUsageException.class, () -> sampleServiceImpl.select(sampleId));
    }

    @Order(8)
    @Test
    @Transactional
    void findList_return_HelloFindListRspDto() {
        // given
        final IntFunction<String> initEmailByNumber =
                number -> String.format("mock%d@email.com", number);
        var sameInsertReqDto =
                SampleInsertReqDto.builder()
                        .name("name")
                        .birthday(parseBirthday("1900-01-01"))
                        .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                        .build();
        final var SAME_COUNT = 10;
        IntStream.range(1, SAME_COUNT + 1)
                .forEach(
                        number ->
                                sampleServiceImpl.insert(
                                        SampleInsertReqDto.builder()
                                                .email(initEmailByNumber.apply(number))
                                                .name(sameInsertReqDto.getName())
                                                .birthday(sameInsertReqDto.getBirthday())
                                                .yyyyMMddHHmmssSSS(sameInsertReqDto.getYyyyMMddHHmmssSSS())
                                                .build()));

        // when
        var rspDto = sampleServiceImpl.selectList(sameInsertReqDto.getName());

        // then
        assertThat(rspDto.getList()).hasSize(SAME_COUNT);

        // id 및 unique 컬럼 외 모든 컬럼 값이 동일하기에 목록 전체 검증은 의미가 없다.
        // 따라서 표본 1개만 랜덤으로 추출하여 검증한다.
        var specimenIdx = new Random(System.currentTimeMillis()).nextInt(SAME_COUNT);
        var specimenRspDto = rspDto.getList().get(specimenIdx);
        assertThat(specimenRspDto.getEmail()).isEqualTo(initEmailByNumber.apply(specimenIdx + 1));
        assertThat(specimenRspDto.getName()).isEqualTo(sameInsertReqDto.getName());
        assertThat(specimenRspDto.getBirthday()).isEqualTo(sameInsertReqDto.getBirthday());
    }

    @Order(9)
    @Test
    @Transactional
    void findList_with_not_exists_return_empty_ArrayList() {
        // given
        final var NOT_EXISTS_MBR_NM = "Not exist!";

        // when
        var rspDto = sampleServiceImpl.selectList(NOT_EXISTS_MBR_NM);

        // then
        assertThat(rspDto.getList()).isNotNull();
        assertThat(rspDto.getList()).isEmpty();
    }

    @Order(10)
    @Test
    @Transactional
    void update_return_HelloUpdateRspDto() {
        // setup
        var sampleInsertRspDto =
                sampleServiceImpl.insert(
                        SampleInsertReqDto.builder()
                                .email("mock@email.com")
                                .name("mock")
                                .birthday(parseBirthday("1900-01-01"))
                                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                                .build());

        // given
        var sampleUpdateReqDto =
                SampleUpdateReqDto.builder()
                        .name("update name")
                        .birthday(parseBirthday("1993-07-24"))
                        .build();

        // when
        var sampleUpdateRspDto =
                sampleServiceImpl.update(sampleInsertRspDto.getId(), sampleUpdateReqDto);

        // then
        assertThat(sampleUpdateRspDto.getName()).isEqualTo(sampleUpdateReqDto.getName());
        assertThat(sampleUpdateRspDto.getBirthday()).isEqualTo(sampleUpdateReqDto.getBirthday());
        assertThat(sampleUpdateRspDto.getEmail()).isEqualTo(sampleInsertRspDto.getEmail());
    }

    @Order(11)
    @Test
    void update_with_not_exist_hello_throw_HelloNotFoundException() {
        // given
        var sampleUpdateReqDto =
                SampleUpdateReqDto.builder()
                        .name("update name")
                        .birthday(parseBirthday("1993-07-24"))
                        .build();
        final var NOT_EXIST_ID = -1L;

        // when-then
        assertThrows(
                SampleNotFoundException.class,
                () -> sampleServiceImpl.update(NOT_EXIST_ID, sampleUpdateReqDto));
    }

    @Order(12)
    @Test
    @Transactional
    void delete_return_SampleDeleteRspDto() {
        // setup
        var sampleInsertRspDto =
                sampleServiceImpl.insert(
                        SampleInsertReqDto.builder()
                                .email("mock@email.com")
                                .name("mock")
                                .birthday(parseBirthday("1900-01-01"))
                                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                                .build());

        // given
        var deleteId = sampleInsertRspDto.getId();

        // when
        var sampleDeleteRspDto = sampleServiceImpl.delete(deleteId);

        // then
        assertThat(sampleDeleteRspDto.getUseYn()).isEqualTo("N");
    }

    @Order(13)
    @Test
    void delete_with_not_exist_id_throw_SampleNotFoundException() {
        // given
        final var NOT_EXIST_ID = -1L;

        // when-then
        assertThrows(SampleNotFoundException.class, () -> sampleServiceImpl.delete(NOT_EXIST_ID));
    }

    private LocalDate parseBirthday(String formatDate) {
        var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(formatDate, dateTimeFormatter);
    }

    private LocalDateTime nowYyyyMMddHHmmssSSS() {
        var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter);
    }
}
