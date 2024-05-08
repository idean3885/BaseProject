package com.dykim.base.service.sample;

import com.dykim.base.dto.sample.SampleDeleteRspDto;
import com.dykim.base.dto.sample.SampleFindListRspDto;
import com.dykim.base.dto.sample.SampleFindRspDto;
import com.dykim.base.dto.sample.SampleInsertReqDto;
import com.dykim.base.dto.sample.SampleInsertRspDto;
import com.dykim.base.dto.sample.SampleUpdateReqDto;
import com.dykim.base.dto.sample.SampleUpdateRspDto;
import javax.validation.Valid;

/**
 *
 *
 * <h3>Sample service</h3>
 *
 * @author dongyoung.kim
 * @since 1.0
 */
public interface SampleService {

    String occurException(boolean isOccur);

    /**
     *
     *
     * <h3>Hello 삽입</h3>
     *
     * <pre>
     *  - 검색 - 검증 - 인서트 방식으로 진행
     *  - 총 두번의 쿼리를 실행하지만 인서트 오류를 명확히 설정하기 위해 위와 같이 작업함.
     *  - 인서트 도중 예외가 발생하는 경우 DataIntegrityViolationException 이 발생
     *  - 이 예외는 인서트, 업데이트 중 발생하는 포괄적인 예외이기 때문에 다른 작업 중에도 발생할 수 있음.
     *  - 따라서 인서트 예외를 특정하여 사이드 이펙트를 줄이기 위해 커스텀 예외를 발생시킨다.
     * </pre>
     *
     * <p>참고) 서비스 메서드에서 Dto 검증 확인을 위해 Bean Validation 설정
     *
     * @param reqDto Hello 추가 요청 Dto
     * @return Hello 추가 응답 Dto | HelloAlreadyExistException
     */
    SampleInsertRspDto insert(@Valid SampleInsertReqDto reqDto);

    SampleFindRspDto select(Long id);

    SampleFindListRspDto selectList(String name);

    SampleUpdateRspDto update(Long id, SampleUpdateReqDto reqDto);

    SampleDeleteRspDto delete(Long id);
}
