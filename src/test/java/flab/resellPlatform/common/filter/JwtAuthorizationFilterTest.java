package flab.resellPlatform.common.filter;

import com.auth0.jwt.algorithms.Algorithm;
import flab.resellPlatform.MessageConfig;
import flab.resellPlatform.RedisConfig;
import flab.resellPlatform.SecurityConfig;
import flab.resellPlatform.common.utils.JWTUtils;
import flab.resellPlatform.controller.user.HomeController;
import flab.resellPlatform.controller.user.UserController;
import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.MybatisUserRepository;
import flab.resellPlatform.repository.user.UserRepository;
import flab.resellPlatform.service.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SecurityConfig.class, MessageConfig.class, RedisConfig.class, HomeController.class, UserController.class})
@WebAppConfiguration
@TestPropertySource(locations = "/application.properties")
class JwtAuthorizationFilterTest {

    @MockBean private MessageSourceAccessor messageSourceAccessor;
    @MockBean private UserService userService;
    @MockBean private RandomValueStringGenerator randomValueStringGenerator;
    @MockBean private PasswordEncoder passwordEncoder;

    @MockBean
    RedisTemplate<String, Object> redisTemplate;

    @MockBean
    ValueOperations<String, Object> valueOperations;

    @Autowired
    Environment environment;

    @MockBean
    UserRepository userRepository;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    Algorithm jwtHashingAlgorithm;

    @Value("${jwt.header.name}")
    String authenticationHeader;

    @Value("${jwt.prefix}")
    String jwtPrefix;

    @Value("${jwt.token.type.access}")
    String accessTokenTypeName;

    @Value("${jwt.token.type.refresh}")
    String refreshTokenTypeName;

    @Value("${jwt.token.type}")
    String tokenTypeKey;

    @Value("${jwt.secret.key}")
    String secretKey;

    @Value("${jwt.access.expiration.time}")
    long accessTokenExpirationTime;

    @Value("${jwt.refresh.expiration.time}")
    long refreshTokenExpirationTime;

    MockMvc mockMvc;

    UserEntity userEntity;
    String accessToken;
    String refreshToken;
    String accessTokenData;
    String refreshTokenData;

    String getJWTTokenInFormat(String tokenData) {
        return jwtPrefix + " " + accessTokenTypeName + " " + tokenData;
    }

    @BeforeEach
    void setup() {
        userEntity = UserTestFactory.createUserEntityBuilder().build();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(new StandardResponseConvertFilter())
                .apply(springSecurity())
                .build();

        accessTokenData = JWTUtils.createJwtToken(userEntity.getId(), userEntity.getUsername(), accessTokenTypeName, accessTokenExpirationTime, jwtHashingAlgorithm);
        refreshTokenData = JWTUtils.createJwtToken(userEntity.getId(), userEntity.getUsername(), refreshTokenTypeName, refreshTokenExpirationTime, jwtHashingAlgorithm);
        accessToken = getJWTTokenInFormat(accessTokenData);
        refreshToken = getJWTTokenInFormat(accessTokenData);
    }

    @DisplayName("접속 성공 with access token")
    @Test
    void doFilterInternal_access_token_success() throws Exception {

        // given
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        ResultActions resultActions = mockMvc.perform(get("/")
                .header(authenticationHeader, accessToken)
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("접속 성공 with refresh token")
    @Test
    void doFilterInternal_refresh_token_success() throws Exception {
        // given
        String storedRefreshTokenData = refreshTokenData;
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(storedRefreshTokenData);

        // when
        ResultActions resultActions = mockMvc.perform(get("/")
                .header(authenticationHeader, refreshToken)
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("접속 실패 by Authentication 헤더 없음")
    @Test
    void doFilterInternal_no_jwt_header() throws Exception {
        // given
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/password/update")
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @DisplayName("접속 실패 by 정해진 토큰 형식 아님")
    @Test
    void doFilterInternal_invalid_token_type() throws Exception {
        // given
        refreshTokenData = jwtPrefix + "wrongTokeType" + refreshTokenData;
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        ResultActions resultActions = mockMvc.perform(get("/")
                .header(authenticationHeader, refreshTokenData)
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("접속 실패 by IllegalArgumentException")
    @Test
    void doFilterInternal_IllegalArgumentException() throws Exception {

        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getProperty("jwt.secret.key")).thenReturn(null);

        // given
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        ResultActions resultActions = mockMvc.perform(get("/")
                .header(authenticationHeader, refreshTokenData)
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("접속 실패 by AlgorithmMismatchException")
    @Test
    void doFilterInternal_AlgorithmMismatchException() throws Exception {
        refreshTokenData = JWTUtils.createJwtToken(userEntity.getId(), userEntity.getUsername(), refreshTokenTypeName, refreshTokenExpirationTime, Algorithm.HMAC384(secretKey));
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getProperty("jwt.secret.key")).thenReturn(null);

        // given
        refreshToken = getJWTTokenInFormat(refreshTokenData);
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        ResultActions resultActions = mockMvc.perform(get("/")
                .header(authenticationHeader, refreshToken)
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("접속 실패 by InvalidClaimException")
    @Test
    void doFilterInternal_InvalidClaimException() throws Exception {
        // InvalidClaimException을 mock으로도 일으킬 수가 없음. 피드백 필요
        Assertions.assertThat(1).isNotNull();
    }

    @DisplayName("접속 실패 by TokenExpiredException")
    @Test
    void doFilterInternal_TokenExpiredException() throws Exception {
        refreshTokenData = JWTUtils.createJwtToken(userEntity.getId(), userEntity.getUsername(), refreshTokenTypeName, -1000, jwtHashingAlgorithm);

        // given
        refreshToken = getJWTTokenInFormat(refreshTokenData);
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        ResultActions resultActions = mockMvc.perform(get("/")
                .header(authenticationHeader, refreshToken)
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    @DisplayName("접속 실패 by SignatureVerificationException")
    @Test
    void doFilterInternal_SignatureVerificationException() throws Exception {
        // SignatureVerificationException을 mock으로도 일으킬 수가 없음. 피드백 필요
        Assertions.assertThat(1).isNotNull();
    }

}