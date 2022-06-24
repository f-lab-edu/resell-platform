package flab.resellPlatform.service.user;

import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    LoginService loginService;

    @DisplayName("로그인 성공")
    @Test
    void doLogin_success() {
        // given
        LoginInfo loginInfo = UserTestFactory.createLoginInfoBuilder().build();
        UserEntity userEntity = UserTestFactory.createUserEntityBuilder().build();
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        Optional<LoginInfo> loginResult = loginService.doLogin(loginInfo);

        // then
        assertThat(loginResult).isNotEmpty();
        assertThat(Optional.of(loginInfo)).isEqualTo(loginResult);
    }

    @DisplayName("유저 이름 존재x")
    @Test
    void doLogin_noUsername() {
        // given
        LoginInfo loginInfo = UserTestFactory.createLoginInfoBuilder().build();
        when(userRepository.findUser(any())).thenReturn(Optional.empty());

        // when
        Optional<LoginInfo> loginResult = loginService.doLogin(loginInfo);

        // then
        assertThat(loginResult).isEmpty();
        assertThat(Optional.of(loginInfo)).isNotEqualTo(loginResult);
    }

    @DisplayName("유저 비밀번호 불일치")
    @Test
    void doLogin_passwordWrong() {
        // given
        LoginInfo loginInfo = UserTestFactory.createLoginInfoBuilder().build();
        when(userRepository.findUser(any())).thenReturn(Optional.empty());

        // when
        Optional<LoginInfo> loginResult = loginService.doLogin(loginInfo);

        // then
        assertThat(loginResult).isEmpty();
    }
}