package com.dykim.base.advice.common;

import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.advice.common.exception.EntityNotFoundException;
import com.dykim.base.member.controller.MemberController;
import com.dykim.base.member.dto.MemberInsertReqDto;
import com.dykim.base.member.service.MemberService;
import com.dykim.base.util.TestAdviceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CommonControllerAdviceTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(memberController)
                .setControllerAdvice(new CommonControllerAdvice())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Order(1)
    @Test
    public void exists_mbrEmail_useY_insert_throw_AlreadyExistsException() throws Exception {
        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml("test@email.com")
                .mbrPswd("pswd")
                .mbrNm("testName")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        given(memberService.insert(any())).willThrow(AlreadyExistsException.class);

        // when
        mockMvc.perform(put("/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(result -> assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                        .isEqualTo(AlreadyExistsException.class))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(2)
    @Test
    public void not_found_member_throw_EntityNotFoundException() throws Exception {
        // given
        given(memberService.delete(any())).willThrow(EntityNotFoundException.class);
        final var NOT_FOUND_MBR_ID = -1L;

        // when
        mockMvc.perform(delete("/member/" + NOT_FOUND_MBR_ID))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(result -> assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                        .isEqualTo(EntityNotFoundException.class))
                .andDo(TestAdviceUtil::printRspDto);
    }

}
