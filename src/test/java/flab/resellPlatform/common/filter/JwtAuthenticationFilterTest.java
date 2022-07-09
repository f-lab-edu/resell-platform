package flab.resellPlatform.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.MessageConfig;
import flab.resellPlatform.SecurityConfig;
import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.repository.user.UserRepository;
import flab.resellPlatform.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SecurityConfig.class, MessageConfig.class})
@WebAppConfiguration
@TestPropertySource(locations = "/application.properties")
class JwtAuthenticationFilterTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserService userService;

    @MockBean
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    ValueOperations<String, Object> valueOperations;

    @Mock
    AuthenticationManager authenticationManager;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    MessageSourceAccessor messageSourceAccessor;

    @Autowired
    Environment environment;

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    ObjectMapper mappper = new ObjectMapper();
    LoginInfo loginInfo;
    PrincipleDetails principleDetails;
    Authentication authentication;

    @BeforeEach
    void setup() {
        mockMvc = mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(new StandardResponseConvertFilter())
                .apply(springSecurity())
                .build();

        loginInfo = UserTestFactory.createLoginInfoBuilder().build();
        principleDetails = UserTestFactory.createPrincipleDetailBuilder().build();
        authentication = UserTestFactory.createAuthentication(principleDetails);
    }


    @DisplayName("로그인 성공")
    @WithMockUser
    @Test
    @DirtiesContext
    void login_success() throws Exception {
        // given
        String body = mappper.writeValueAsString(loginInfo);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);

        // when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

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

        resultActions.andExpect(status().isUnauthorized());
    }
}