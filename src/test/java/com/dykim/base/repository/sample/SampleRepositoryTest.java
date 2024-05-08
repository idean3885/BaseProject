package com.dykim.base.repository.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dykim.base.advice.sample.exception.SampleAuditorAwareException;
import com.dykim.base.config.entity.BaseAuditorAware;
import com.dykim.base.dto.sample.SampleUpdateReqDto;
import com.dykim.base.entity.sample.Sample;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("internal")
@SpringBootTest
public class SampleRepositoryTest {

    @Autowired SampleRepository sampleRepository;

    @Autowired BaseAuditorAware baseAuditorAware;

    /**
     *
     *
     * <h3>각 테스트케이스 별 후처리</h3>
     *
     * 케이스 종료 후 데이터를 삭제하여 다른 케이스에 영향을 주지 않도록 한다.
     *
     * <pre>
     *  - Spring 가이드에 따르면 온전한 동작을 하지 않을 수 있기에 믿지 말라고 되어있으나 근거가 부족함.
     *  - 따라서 해당 예제에서는 @Transactional 선언 대신 공통 후처리로 진행해봄.
     * </pre>
     *
     * <b>참고)후처리가 없는 경우 케이스 별 @Transactional 선언하여 종료 후 롤백되도록 해야한다.</b>
     */
    @AfterEach
    public void clearAllHello() {
        sampleRepository.deleteAll();
    }

    @Order(1)
    @Test
    public void findById_return_Hello() {
        // given
        var mockHello =
                sampleRepository.save(
                        Sample.builder()
                                .email("mock@email.com")
                                .name("mockName1")
                                .birthday(LocalDate.parse("1993-07-24"))
                                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                                .useYn("Y")
                                .build());

        // when
        var helloOps = sampleRepository.findById(mockHello.getId());

        // then
        assertThat(helloOps.isPresent()).isTrue();

        var hello = helloOps.get();
        assertThat(hello.getEmail()).isEqualTo(mockHello.getEmail());
        assertThat(hello.getName()).isEqualTo(mockHello.getName());
        assertThat(hello.getBirthday()).isEqualTo(mockHello.getBirthday());
        assertBaseEntityFind(hello);
    }

    @Order(2)
    @Test
    public void findAllByName_return_Hello_list() {
        // given
        final IntFunction<String> initEmailByNumber =
                number -> String.format("mock%d@email.com", number);
        var sameSaveHello =
                Sample.builder()
                        .name("mockName1")
                        .birthday(LocalDate.parse("1993-07-24"))
                        .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                        .useYn("Y")
                        .build();
        final var SAME_COUNT = 10;
        IntStream.range(1, SAME_COUNT + 1)
                .forEach(
                        number ->
                                sampleRepository.save(
                                        Sample.builder()
                                                .email(initEmailByNumber.apply(number))
                                                .name(sameSaveHello.getName())
                                                .birthday(sameSaveHello.getBirthday())
                                                .yyyyMMddHHmmssSSS(sameSaveHello.getYyyyMMddHHmmssSSS())
                                                .useYn(sameSaveHello.getUseYn())
                                                .build()));

        // when
        var helloListOps = sampleRepository.findAllByName(sameSaveHello.getName());

        // then
        assertThat(helloListOps.isPresent()).isTrue();

        var helloList = helloListOps.get();
        assertThat(helloList.size()).isEqualTo(SAME_COUNT);
        var specimenIdx = new Random(System.currentTimeMillis()).nextInt(SAME_COUNT);
        var specimenHello = helloList.get(specimenIdx);
        assertThat(specimenHello.getEmail()).isEqualTo(initEmailByNumber.apply(specimenIdx + 1));
        assertThat(specimenHello.getName()).isEqualTo(sameSaveHello.getName());
        assertThat(specimenHello.getBirthday()).isEqualTo(sameSaveHello.getBirthday());
        assertBaseEntityFind(specimenHello);
    }

    @Order(3)
    @Test
    public void save_return_Hello() {
        // given
        var hello =
                Sample.builder()
                        .email("insertEmail@email.com")
                        .name("insertName")
                        .birthday(LocalDate.parse("1993-07-24"))
                        .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                        .useYn("Y")
                        .build();

        // when
        var insertedHello = sampleRepository.save(hello);

        // then
        assertThat(insertedHello.getId()).isNotNull();
        assertThat(insertedHello.getEmail()).isEqualTo(hello.getEmail());
        assertThat(insertedHello.getName()).isEqualTo(hello.getName());
        assertThat(insertedHello.getBirthday()).isEqualTo(hello.getBirthday());
        assertBaseEntitySave(insertedHello);
    }

    @Order(4)
    @Test
    public void save_with_duplicate_email_throw_DataIntegrityViolationException() {
        // setup
        var mockHello =
                sampleRepository.save(
                        Sample.builder()
                                .email("mock@email.com")
                                .name("mockName1")
                                .birthday(LocalDate.parse("1993-07-24"))
                                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                                .useYn("Y")
                                .build());

        // given
        var hello =
                Sample.builder()
                        .email(mockHello.getEmail())
                        .name("insertName")
                        .birthday(LocalDate.parse("1993-07-24"))
                        .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                        .useYn("Y")
                        .build();

        // when-then
        assertThrows(DataIntegrityViolationException.class, () -> sampleRepository.save(hello));
    }

    @Order(5)
    @Test
    public void save_with_empty_require_parameter_throw_ConstraintViolationException() {
        // given
        var hello =
                Sample.builder()
                        .email("insertNameTest@email.com")
                        .birthday(LocalDate.parse("1993-07-24"))
                        .build();

        // when-then
        assertThrows(ConstraintViolationException.class, () -> sampleRepository.save(hello));
    }

    @Order(6)
    @Test
    public void update_with_DynamicUpdate_return_HelloUpdateRspDto() {
        // setup
        var mockHello =
                sampleRepository.save(
                        Sample.builder()
                                .email("mock@email.com")
                                .name("mockName1")
                                .birthday(LocalDate.parse("1993-07-24"))
                                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                                .useYn("Y")
                                .build());

        // given
        var helloUpdateReqDto =
                SampleUpdateReqDto.builder()
                        .name("update Name")
                        .birthday(LocalDate.parse("1900-07-24"))
                        .build();

        // when
        var hello = sampleRepository.save(mockHello.update(helloUpdateReqDto));

        // then
        assertThat(hello.getId()).isEqualTo(mockHello.getId());
        assertThat(hello.getName()).isEqualTo(helloUpdateReqDto.getName());
        assertThat(hello.getBirthday()).isEqualTo(helloUpdateReqDto.getBirthday());
        assertBaseEntityUpdate(hello);

        var findHelloOps = sampleRepository.findById(hello.getId());
        assertThat(findHelloOps.isPresent()).isTrue();

        var findHello = findHelloOps.get();
        assertThat(findHello.getName()).isEqualTo(helloUpdateReqDto.getName());
        assertThat(findHello.getBirthday()).isEqualTo(helloUpdateReqDto.getBirthday());
        assertBaseEntityFind(findHello);
    }

    @Order(7)
    @Test
    public void delete_with_DynamicUpdate_return_Hello() {
        // setup
        var mockHello =
                sampleRepository.save(
                        Sample.builder()
                                .email("mock@email.com")
                                .name("mockName1")
                                .birthday(LocalDate.parse("1993-07-24"))
                                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                                .useYn("Y")
                                .build());

        // given
        mockHello.delete();

        // when
        var hello = sampleRepository.save(mockHello);

        // then
        assertThat(hello.getId()).isEqualTo(mockHello.getId());
        assertThat(hello.getUseYn()).isEqualTo("N");
        assertBaseEntityDelete(hello);

        var findHelloOps = sampleRepository.findById(mockHello.getId());
        assertThat(findHelloOps.isPresent()).isTrue();

        var findHello = findHelloOps.get();
        assertThat(findHello.getUseYn()).isEqualTo("N");
        assertBaseEntityFind(findHello);
    }

    private LocalDateTime nowYyyyMMddHHmmssSSS() {
        var localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return LocalDateTime.parse(
                LocalDateTime.now().format(localDateTimeFormat), localDateTimeFormat);
    }

    private void assertBaseEntitySave(Sample sample) {
        assertBaseEntity(sample, "SAVE");
    }

    private void assertBaseEntityFind(Sample sample) {
        assertBaseEntity(sample, "FIND");
    }

    private void assertBaseEntityUpdate(Sample sample) {
        assertBaseEntity(sample, "UPDATE");
    }

    private void assertBaseEntityDelete(Sample sample) {
        assertBaseEntity(sample, "DELETE");
    }

    private void assertBaseEntity(Sample sample, String type) {
        // given
        var auditorId =
                baseAuditorAware
                        .getCurrentAuditor()
                        .orElseThrow(
                                () -> new SampleAuditorAwareException("Not found sessionUserId from Auditor"));

        // then
        // 1) common
        assertThat(sample.getCreatedDateTime()).isNotNull();
        assertThat(sample.getUpdatedDateTime()).isNotNull();

        // 2) each case
        switch (type) {
            case "SAVE":
                assertThat(sample.getUpdatedDateTime()).isEqualTo(sample.getCreatedDateTime());
                assertThat(sample.getCreatedId()).isEqualTo(auditorId);
                assertThat(sample.getUpdatedId()).isEqualTo(auditorId);
                break;
            case "FIND":
                assertThat(sample.getUpdatedDateTime()).isAfterOrEqualTo(sample.getCreatedDateTime());
                break;
            case "UPDATE":
            case "DELETE":
                assertThat(sample.getUpdatedId()).isEqualTo(auditorId);
                assertThat(sample.getUpdatedDateTime()).isAfterOrEqualTo(sample.getCreatedDateTime());
                break;
            default:
                assert false;
        }
    }
}
