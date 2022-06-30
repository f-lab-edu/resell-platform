package flab.resellPlatform.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.repository.user.UserRepository;
import flab.resellPlatform.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
public class JwtLoginTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    UserService userService;

    @MockBean
    AuthenticationManager authenticationManager;

    @Autowired
    MessageSourceAccessor messageSourceAccessor;

    @Autowired
    Environment environment;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mappper = new ObjectMapper();
    LoginInfo loginInfo;
    PrincipleDetails principleDetails;
    Authentication authentication;

    @BeforeEach
    void setup() {
        loginInfo = UserTestFactory.createLoginInfoBuilder().build();
        principleDetails = UserTestFactory.createPrincipleDetailBuilder().build();
        authentication = UserTestFactory.createAuthentication(principleDetails);
    }


    @DisplayName("로그인 성공")
    @WithMockUser
    @Test
    void login_success() throws Exception {
        // given
        String body = mappper.writeValueAsString(loginInfo);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        // then
        StandardResponse standardResponse = StandardResponse.builder()
                .data(Map.of())
                .message(messageSourceAccessor.getMessage("common.login.succeeded"))
                .build();

        resultActions.andExpect(status().isOk());
    }

    @DisplayName("로그인 실패 by 유저 정보 없음")
    @Test
    void login_failure() throws Exception {
        // given
        String body = mappper.writeValueAsString(loginInfo);
        when(authenticationManager.authenticate(any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new BadCredentialsException("");
            }
        });

        // when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        // then
        StandardResponse standardResponse = StandardResponse.builder()
                .data(Map.of())
                .message(messageSourceAccessor.getMessage("common.login.needed"))
                .build();

        resultActions.andExpect(status().isUnauthorized());
    }
}
