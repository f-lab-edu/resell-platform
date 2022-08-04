package flab.integrationtest.usernameinquiry;

import flab.integrationtest.AbstractDockerComposeBasedTest;
import flab.resellPlatform.ResellPlatformApplication;
import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ResellPlatformApplication.class)
@AutoConfigureMockMvc
@Transactional
public class UsernameInquiryIntegrationTest extends AbstractDockerComposeBasedTest {

    UserDTO userDTO;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userDTO = UserTestFactory.createUserDTOBuilder().build();
    }

    @DisplayName("성공 시나리오: 유저 서비스는 핸드폰 번호가 매칭되는 아이디가 있으면 이를 반환한다.")
    @Test
    void usernameInquiry_success() throws Exception {
        // given
        userServiceImpl.createUser(userDTO);

        // when
        String query = userDTO.getPhoneNumber();
        ResultActions resultActions = mockMvc.perform(get("/users/usernameInquiry")
                .queryParam("phoneNumber", query)
                .with(csrf()));

        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("실패 시나리오: 유저 서비스는 핸드폰 번호가 매칭되는 아이디가 없으면 실패 메세지를 반환한다.")
    @Test
    void usernameInquiry_failedByNotExistingPhoneNumber() throws Exception {
        // given
        // when
        String query = userDTO.getPhoneNumber();
        ResultActions resultActions = mockMvc.perform(get("/users/usernameInquiry")
                .queryParam("phoneNumber", query)
                .with(csrf()));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
}
