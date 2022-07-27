package flab.IntegrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.ResellPlatformApplication;
import flab.resellPlatform.data.UserTestFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ResellPlatformApplication.class)
@Transactional
public class IntegrationTest extends AbstractDockerComposeBasedTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;
    String createUserBody;
    String loginBody;

    @BeforeEach
    void initialize() throws JsonProcessingException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        loginBody = objectMapper.writeValueAsString(UserTestFactory.createLoginInfoBuilder().build());
        createUserBody = objectMapper.writeValueAsString(UserTestFactory.createUserDTOBuilder().build());
    }

    @DisplayName("데이터베이스 작동 여부 확인 by 회원가입")
    @Test
    void checkDatabaseWorks() throws Exception {
        // when
        ResultActions resultActions = doRestAPIPostCall("/users/create", createUserBody);

        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("캐시, 세션 서버 작동 여부 확인 by 로그인")
    @Test
    void checkCacheServerAndSessionServerWorking() throws Exception {
        // when
        doRestAPIPostCall("/users/create", createUserBody);
        ResultActions resultActions = doRestAPIPostCall("/login", loginBody);

        // then
        resultActions.andExpect(status().isOk());
    }

    @NotNull
    private ResultActions doRestAPIPostCall(String url, String body) throws Exception {
        ResultActions resultActions = mockMvc.perform(post(url)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));
        return resultActions;
    }
}
