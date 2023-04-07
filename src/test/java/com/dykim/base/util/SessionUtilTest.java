package com.dykim.base.util;

import com.dykim.base.enums.AuthorityRole;
import com.dykim.base.member.dto.MemberSessionVo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * <h3>SessionUtilTest</h3>
 * 세션 유틸 테스트 클래스
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SessionUtilTest {

    @Order(1)
    @Test
    public void call_getMemberSessionVo_valid_session_return_MemberSessionVo() {
        // given
        var mockMemberSessionVo = MemberSessionVo.builder()
                .mbrId(1L)
                .mbrNm("mockNm")
                .mbrEml("mock@email.com")
                .authorityRole(AuthorityRole.DEVELOPER)
                .build();
        var mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionUtil.MEMBER_SESSION_DATA, mockMemberSessionVo);
        given(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession(false)).willReturn(mockHttpSession);

        // when
        var memberSessionVo = SessionUtil.getMemberSessionVo();

        // then
        assertThat(memberSessionVo.getMbrId()).isEqualTo(mockMemberSessionVo.getMbrId());
        assertThat(memberSessionVo.getMbrNm()).isEqualTo(mockMemberSessionVo.getMbrNm());
        assertThat(memberSessionVo.getMbrEml()).isEqualTo(mockMemberSessionVo.getMbrEml());
        assertThat(memberSessionVo.getAuthorityRole()).isEqualTo(mockMemberSessionVo.getAuthorityRole());
    }

}
