package flab.integrationtest.userapi;

import flab.integrationtest.AbstractDockerComposeBasedTest;
import flab.resellPlatform.ResellPlatformApplication;
import flab.resellPlatform.exception.user.PhoneNumberNotFoundException;
import flab.utils.UserTestFactory;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ResellPlatformApplication.class)
@AutoConfigureMockMvc
@Transactional
public class UserCreateIntegrationTest extends AbstractDockerComposeBasedTest {

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

    @DisplayName("성공 시나리오: 고객이 유저 서비스에 회원 가입을 요청한다.")
    @Test
    void userCreate_success() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(userDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(csrf()));


        // then
        resultActions.andExpect(status().isOk());

        String usernameFound = userServiceImpl.findUsername(userDTO.getPhoneNumber());
        assertThat(usernameFound).isEqualTo(userDTO.getUsername());
    }

    @DisplayName("실패 시나리오: 고객은 username, password, phone, email, shoesize를 필수로 입력한다.")
    @Test
    void userCreate_failedByInvalidInput() throws Exception {
        // given
        userDTO = UserTestFactory.createUserDTOBuilder()
                .username("")
                .password("")
                .build();
        String body = objectMapper.writeValueAsString(userDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(csrf()));

        // then
        resultActions.andExpect(status().isBadRequest());
        Assertions.assertThrows(PhoneNumberNotFoundException.class,
                () -> {
                    userServiceImpl.findUsername(userDTO.getPhoneNumber());
                });
    }

    @DisplayName("실패 시나리오: 유저 서비스는 각 고객마다 고유한 username을 보장해야 한다.")
    @Test
    void userCreate_failedByDuplicateUsername() throws Exception {

        // given
        userServiceImpl.createUser(userDTO);
        String body = objectMapper.writeValueAsString(userDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(csrf()));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
}
