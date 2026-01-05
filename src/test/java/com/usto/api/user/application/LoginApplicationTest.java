package com.usto.api.user.application;

import com.usto.api.common.exception.LoginFailedException;
import com.usto.api.user.domain.model.LoginUser;
import com.usto.api.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("로그인 애플리케이션 테스트")
class LoginApplicationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginApplication loginApplication;

    private LoginUser testUser;
    private String testUsrId;
    private String testRawPassword;
    private String testEncodedPassword;

    @BeforeEach
    void setUp() {
        testUsrId = "testuser";
        testRawPassword = "password123";
        testEncodedPassword = "$2a$10$encodedPassword";

        testUser = LoginUser.forLogin(
                testUsrId,
                testEncodedPassword,
                "테스트유저"
        );
    }

    @Test
    @DisplayName("올바른 아이디와 비밀번호로 로그인 성공")
    void login_WithValidCredentials_Success() {
        // given
        given(userRepository.loadByUsrId(testUsrId))
                .willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(testRawPassword, testEncodedPassword))
                .willReturn(true);

        // when
        LoginUser result = loginApplication.login(testUsrId, testRawPassword);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsrId()).isEqualTo(testUsrId);
        assertThat(result.getUsrNm()).isEqualTo("테스트유저");
        verify(userRepository).loadByUsrId(testUsrId);
        verify(passwordEncoder).matches(testRawPassword, testEncodedPassword);
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인 시도 시 예외 발생")
    void login_WithNonExistentUserId_ThrowsException() {
        // given
        given(userRepository.loadByUsrId(anyString()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loginApplication.login("nonexistent", testRawPassword))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도 시 예외 발생")
    void login_WithWrongPassword_ThrowsException() {
        // given
        given(userRepository.loadByUsrId(testUsrId))
                .willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(anyString(), anyString()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> loginApplication.login(testUsrId, "wrongpassword"))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
}
