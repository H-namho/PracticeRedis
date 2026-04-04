package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.SignupRequestDto;
import com.example.swaggerprac.exception.GlobalExceptionHandler;
import com.example.swaggerprac.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void 회원가입_성공() throws Exception {
        SignupRequestDto requestDto = new SignupRequestDto(
                "tester",
                "password123",
                "tester@example.com",
                20
        );

        when(authService.signup(any(SignupRequestDto.class))).thenReturn(1L);

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(authService).signup(any(SignupRequestDto.class));
    }

    @Test
    void 회원가입_검증실패_이메일형식오류() throws Exception {
        SignupRequestDto requestDto = new SignupRequestDto(
                "tester",
                "password123",
                "not-an-email",
                20
        );

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void 회원가입_중복사용자_예외응답() throws Exception {
        SignupRequestDto requestDto = new SignupRequestDto(
                "tester",
                "password123",
                "tester@example.com",
                20
        );

        doThrow(new IllegalArgumentException("이미 존재하는 사용자 이름입니다."))
                .when(authService).signup(any(SignupRequestDto.class));

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 사용자 이름입니다."));
    }
}
