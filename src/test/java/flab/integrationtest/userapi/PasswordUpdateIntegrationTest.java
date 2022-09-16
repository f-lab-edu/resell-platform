package flab.integrationtest.userapi;

import flab.integrationtest.AbstractDockerComposeBasedTest;
import flab.resellPlatform.ResellPlatformApplication;
import flab.resellPlatform.common.response.StandardResponse;
import flab.utils.UserTestFactory;
import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ResellPlatformApplication.class)
@AutoConfigureMockMvc
@Transactional
public class PasswordUpdateIntegrationTest extends AbstractDockerComposeBasedTest {

    UserDTO userDTO;
    LoginInfo loginInfo;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    Environment environment;

    @Autowired
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userDTO = UserTestFactory.createUserDTOBuilder().build();
        loginInfo = UserTestFactory.createLoginInfoBuilder().build();
    }

    @DisplayName("비밀번호 변경, 성공 시나리오")
    @Test
    void usernameUpdate_success() throws Exception {
        // given
        // 회원가입 돼있는 상태
        userService.createUser(userDTO);
        userDTO = UserTestFactory.createUserDTOBuilder().build();
        
        // 로그인 된 상태
        MvcResult mvcResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInfo)))
                .andExpect(status().isOk())
                .andReturn();

        // when
        LoginInfo changeWantedLoginInfo = getChangedPasswordLoginInfo(loginInfo.getPassword() + "change password");
        ResultActions resultActions = mockMvc.perform(post("/users/password/update")
                .contentType(MediaType.APPLICATION_JSON)
                        .header(environment.getProperty("jwt.header.name"), getLoginHeaderData(mvcResult))
                .content(objectMapper.writeValueAsString(changeWantedLoginInfo))
                .with(csrf()));

        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("비밀번호 변경, 실패 시나리오, 비로그인 상태")
    @Test
    void usernameUpdate_failedByInvalidInput() throws Exception {
        // given
        // when
        String body = objectMapper.writeValueAsString(loginInfo);

        ResultActions resultActions = mockMvc.perform(post("/users/password/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginInfo))
                .with(csrf()));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    LoginInfo getChangedPasswordLoginInfo(String changedPassword) {
        return UserTestFactory.createLoginInfoBuilder()
                .username(userDTO.getUsername())
                .password(changedPassword)
                .build();
    }

    @NotNull
    private String getLoginHeaderData(MvcResult mvcResult) throws IOException {
        String responseContentAsString = mvcResult.getResponse().getContentAsString();
        StandardResponse responseBody = objectMapper.readValue(responseContentAsString, StandardResponse.class);
        String tokenData = (String)responseBody.getData().get(environment.getProperty("jwt.token.type.access"));

        String loginHeaderData = environment.getProperty("jwt.prefix") + " " +
                environment.getProperty("jwt.token.type.access") + " " +
                tokenData;
        return loginHeaderData;
    }

}
