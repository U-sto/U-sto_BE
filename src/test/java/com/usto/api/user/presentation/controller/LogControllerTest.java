package com.usto.api.user.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.common.exception.GlobalExceptionHandler;
import com.usto.api.common.exception.LoginFailedException;
import com.usto.api.user.application.LoginApplication;
import com.usto.api.user.domain.model.LoginUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LogController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("로그인 컨트롤러 테스트")
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginApplication loginApplication;

    @Test
    @DisplayName("올바른 요청으로 로그인 성공")
    void login_WithValidRequest_Success() throws Exception {
        // given
        String usrId = "testuser";
        String password = "password123";
        
        LoginUser loginUser = LoginUser.forLogin(usrId, "encoded", "테스트유저");
        given(loginApplication.login(usrId, password))
                .willReturn(loginUser);

        String requestBody = """
                {
                    "usrId": "testuser",
                    "pwd": "password123"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.data.usrId").value("testuser"))
                .andExpect(jsonPath("$.data.usrNm").value("테스트유저"));
    }

    @Test
    @DisplayName("빈 아이디로 로그인 시도 시 검증 실패")
    void login_WithEmptyUserId_ValidationFails() throws Exception {
        // given
        String requestBody = """
                {
                    "usrId": "",
                    "pwd": "password123"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."));
    }

    @Test
    @DisplayName("빈 비밀번호로 로그인 시도 시 검증 실패")
    void login_WithEmptyPassword_ValidationFails() throws Exception {
        // given
        String requestBody = """
                {
                    "usrId": "testuser",
                    "pwd": ""
                }
                """;

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."));
    }

    @Test
    @DisplayName("잘못된 인증 정보로 로그인 시도 시 401 에러")
    void login_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        // given
        given(loginApplication.login(anyString(), anyString()))
                .willThrow(new LoginFailedException());

        String requestBody = """
                {
                    "usrId": "testuser",
                    "pwd": "wrongpassword"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
    }
}
