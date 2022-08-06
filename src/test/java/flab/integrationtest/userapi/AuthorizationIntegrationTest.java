package flab.integrationtest.userapi;

import flab.integrationtest.AbstractDockerComposeBasedTest;
import flab.resellPlatform.ResellPlatformApplication;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.Role;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import flab.utils.CustomAssertionUtils;
import flab.utils.CustomMockMvcUtils;
import flab.utils.TestController;
import flab.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.Valid;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ResellPlatformApplication.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestController.class)
@Transactional
public class AuthorizationIntegrationTest extends AbstractDockerComposeBasedTest {

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

    @DisplayName("인가, 성공 시나리오")
    @Test
    void login_success() throws Exception {

        // given
        // 가입된 사용자
        userService.createUser(userDTO);

        // 로그인된 사용자
        String accessTokenData = CustomMockMvcUtils.getAccessTokenDataByMockMvcLogin(mockMvc, objectMapper.writeValueAsString(loginInfo), objectMapper, environment);
        
        // when
        // 유스케이스/인가/Happy path
        ResultActions resultActions = mockMvc.perform(get("/test")
                .header(environment.getProperty("jwt.header.name"), getJWTTokenInFormat(accessTokenData))
                .with(csrf()));

        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("인가, 실패 시나리오, 요청에 인가 정보가 없는 경우")
    @Test
    void login_failed() throws Exception {
        // given
        // 가입된 사용자
        userService.createUser(userDTO);

        // 로그인된 사용자
        String accessTokenData = CustomMockMvcUtils.getAccessTokenDataByMockMvcLogin(mockMvc, objectMapper.writeValueAsString(loginInfo), objectMapper, environment);

        // when
        // 유스케이스/인가/Fail path
        // 1. 인가가 필요한 요청에 인가 정보가 없다면, 실패 메세지를 반환한다.
        ResultActions resultActions = mockMvc.perform(get("/test")
                .with(csrf()));

        // then
        // 실패 코드 반환
        resultActions.andExpect(status().isForbidden());
        // 실패 메세지 반환
        CustomAssertionUtils.assertStandardResponseBodyContainsMessage(objectMapper, resultActions, messageSourceAccessor.getMessage("common.access.denied"));
    }

    String getJWTTokenInFormat(String tokenData) {
        return environment.getProperty("jwt.prefix") + " " + environment.getProperty("jwt.token.type.access") + " " + tokenData;
    }
}
