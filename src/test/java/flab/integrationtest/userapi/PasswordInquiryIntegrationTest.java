package flab.integrationtest.userapi;

import flab.integrationtest.AbstractDockerComposeBasedTest;
import flab.resellPlatform.ResellPlatformApplication;
import flab.utils.UserTestFactory;
import flab.resellPlatform.domain.user.StrictLoginInfo;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ResellPlatformApplication.class)
@AutoConfigureMockMvc
@Transactional
public class PasswordInquiryIntegrationTest extends AbstractDockerComposeBasedTest {

    UserDTO userDTO;
    StrictLoginInfo strictLoginInfo;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userDTO = UserTestFactory.createUserDTOBuilder().build();
        strictLoginInfo = UserTestFactory.createStrictLoginInfoBuilder().build();
    }

    @DisplayName("성공 시나리오: 고객이 유저 서비스에 유저네임, 패스워드, 폰번호, 이메일을 전달한다.")
    @Test
    void usernameInquiry_success() throws Exception {
        // given
        userService.createUser(userDTO);

        // when
        String body = objectMapper.writeValueAsString(strictLoginInfo);
        ResultActions resultActions = mockMvc.perform(post("/users/password/inquiry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(csrf()));

        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("실패 시나리오: 필요한 정보를 전달하지 않으면 오류를 반환한다.")
    @Test
    void usernameInquiry_failedByInvalidInput() throws Exception {
        // given
        // when
        strictLoginInfo = UserTestFactory.createStrictLoginInfoBuilder()
                .username("")
                .build();
        String body = objectMapper.writeValueAsString(strictLoginInfo);
        ResultActions resultActions = mockMvc.perform(post("/users/password/inquiry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(csrf()));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("실패 시나리오: 전달된 정보와 맞는 유저 정보가 없다면 패스워드 실패 메세지를 반환한다.")
    @Test
    void usernameInquiry_failedByMismatchingUserInfo() throws Exception {
        // given
        // when
        String body = objectMapper.writeValueAsString(strictLoginInfo);
        ResultActions resultActions = mockMvc.perform(post("/users/password/inquiry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(csrf()));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
}
