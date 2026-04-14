package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.auth.LoginRequestDto;
import com.example.swaggerprac.dto.auth.LoginResponseDto;
import com.example.swaggerprac.dto.auth.SignupRequestDto;
import com.example.swaggerprac.exception.ConflictException;
import com.example.swaggerprac.exception.GlobalExceptionHandler;
import com.example.swaggerprac.exception.UnauthorizedException;
import com.example.swaggerprac.security.jwt.JwtAuthenticationFilter;
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

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void signupSuccess() throws Exception {
        SignupRequestDto requestDto = new SignupRequestDto(
                "tester",
                "password123",
                "tester@example.com",
                20
        );

        when(authService.signup(any(SignupRequestDto.class))).thenReturn(1L);

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(authService).signup(any(SignupRequestDto.class));
    }

    @Test
    void signupValidationFailure() throws Exception {
        SignupRequestDto requestDto = new SignupRequestDto(
                "tester",
                "password123",
                "not-an-email",
                20
        );

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void signupDuplicateUserReturnsConflict() throws Exception {
        SignupRequestDto requestDto = new SignupRequestDto(
                "tester",
                "password123",
                "tester@example.com",
                20
        );

        doThrow(new ConflictException("Username already exists."))
                .when(authService).signup(any(SignupRequestDto.class));

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already exists."));
    }

    @Test
    void loginSuccess() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("tester", "password123");
        LoginResponseDto responseDto = new LoginResponseDto(
                "Bearer",
                "access-token",
                "refresh-token"
        );

        when(authService.login(any(LoginRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grantType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void loginFailureReturnsUnauthorized() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("tester", "wrong-password");

        doThrow(new UnauthorizedException("Invalid username or password."))
                .when(authService).login(any(LoginRequestDto.class));

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password."));
    }
}
