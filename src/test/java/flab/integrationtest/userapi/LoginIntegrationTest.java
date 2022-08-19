package flab.integrationtest.userapi;

import flab.integrationtest.AbstractDockerComposeBasedTest;
import flab.resellPlatform.ResellPlatformApplication;
import flab.utils.CustomAssertionUtils;
import flab.utils.UserTestFactory;
import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = ResellPlatformApplication.class)
@AutoConfigureMockMvc
@Transactional
public class LoginIntegrationTest extends AbstractDockerComposeBasedTest {

    @Autowired
    Environment environment;

    @Autowired
    MessageSourceAccessor messageSourceAccessor;

    @Autowired
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    UserDTO userDTO;
    LoginInfo loginInfo;

    @BeforeEach
    void setUp() {
        userDTO = UserTestFactory.createUserDTOBuilder().build();
        loginInfo = UserTestFactory.createLoginInfoBuilder().build();
    }

    @DisplayName("로그인, 성공 시나리오")
    @Test
    void login_success() throws Exception {
        // given
        // 가입된 사용자
        userService.createUser(userDTO);

        // when
        // 유스케이스/로그인/Happy path
        // 1. 사용자는 아이디와 비밀번호를 유저 서비스에 전달한다.
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginInfo))
                .with(csrf()));

        // then
        // 3. 인증된 사용자를 입증하는 토큰을 클라이언트에 반환한다.
        // 응답 코드 확인
        resultActions.andExpect(status().isOk());
        // 토큰 반환 확인
        CustomAssertionUtils.assertStandardResponseBodyContainsData(objectMapper, resultActions, environment.getProperty("jwt.token.type.access"));
    }

    @DisplayName("로그인, 실패 시나리오")
    @Test
    void login_failed() throws Exception {
        // given
        // 가입되지 않은 사용자
        // userService.createUser(userDTO);

        // when
        // 유스케이스/로그인/Fail path
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginInfo))
                .with(csrf()));

        // then
        // 1. 서비스 이용자 인증이 실패할 시 실패 메세지를 반환한다.
        // 응답 코드 확인
        resultActions.andExpect(status().isUnauthorized());
        // 실패 메세지 확인
        CustomAssertionUtils.assertStandardResponseBodyContainsMessage(objectMapper, resultActions, messageSourceAccessor.getMessage("jwt.invalid.user"));
    }
}
