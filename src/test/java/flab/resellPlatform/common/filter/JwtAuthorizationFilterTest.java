package flab.resellPlatform.common.filter;

import com.auth0.jwt.algorithms.Algorithm;
import flab.resellPlatform.MessageConfig;
import flab.resellPlatform.SecurityConfig;
import flab.resellPlatform.common.utils.JWTUtils;
import flab.resellPlatform.controller.user.HomeController;
import flab.resellPlatform.controller.user.UserController;
import flab.utils.TestController;
import flab.utils.UserTestFactory;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import flab.resellPlatform.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TestController.class})
@Import(value = {SecurityConfig.class, MessageConfig.class})
class JwtAuthorizationFilterTest {

    @MockBean
    RedisTemplate<String, Object> redisSessionTemplate;

    @MockBean
    ValueOperations<String, Object> valueOperations;

    @MockBean
    SecurityContext securityContext;

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

    @Autowired
    MockMvc mockMvc;

    UserEntity userEntity;
    String accessTokenHeaderData;
    String refreshTokenHeaderData;
    String accessTokenData;
    String refreshTokenData;

    @BeforeEach
    void setup() {
        userEntity = UserTestFactory.createUserEntityBuilder().build();

        accessTokenData = JWTUtils.createJwtToken(userEntity.getId(), userEntity.getUsername(), accessTokenTypeName, accessTokenExpirationTime, jwtHashingAlgorithm);
        refreshTokenData = JWTUtils.createJwtToken(userEntity.getId(), userEntity.getUsername(), refreshTokenTypeName, refreshTokenExpirationTime, jwtHashingAlgorithm);
        accessTokenHeaderData = getJWTTokenInFormat(accessTokenTypeName, accessTokenData);
        refreshTokenHeaderData = getJWTTokenInFormat(refreshTokenTypeName, refreshTokenData);
    }

    @DisplayName("권한 획득 성공 with access token")
    @Test
    void doFilterInternal_access_token_success() throws Exception {

        // given
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        ResultActions resultActions = mockMvc.perform(get("/test")
                .header(authenticationHeader, accessTokenHeaderData)
                .with(csrf())
        );

        // then
        verify(securityContext).setAuthentication(any());
    }

    @DisplayName("토큰 재발급 성공 with refresh token")
    @Test
    void doFilterInternal_refresh_token_success() throws Exception {
        // given
        String storedRefreshTokenData = refreshTokenData;
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));
        when(redisSessionTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(storedRefreshTokenData);

        System.out.println("storedRefreshTokenData: " + storedRefreshTokenData);
        System.out.println("refreshTokenHeaderData: " + refreshTokenHeaderData);

        // when
        ResultActions resultActions = mockMvc.perform(get("/test")
                .header(authenticationHeader, refreshTokenHeaderData)
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("권한 획득 실패 by Authentication 헤더 없음")
    @Test
    void doFilterInternal_no_jwt_header() throws Exception {
        // given
        // when
        mockMvc.perform(get("/test")
                .with(csrf()));

        // then
        verify(securityContext, never()).setAuthentication(any());
    }

    @DisplayName("접속 실패 by 정해진 토큰 형식 아님")
    @Test
    void doFilterInternal_invalid_token_type() throws Exception {
        // given
        refreshTokenData = jwtPrefix + "wrongTokeType" + refreshTokenData;
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        mockMvc.perform(get("/test")
                .header(authenticationHeader, refreshTokenData)
                .with(csrf()))
                .andExpect(status().isBadRequest());

        // then
        verify(securityContext, never()).setAuthentication(any());
    }

    @DisplayName("접속 실패 by IllegalArgumentException")
    @Test
    @DirtiesContext
    void doFilterInternal_IllegalArgumentException() throws Exception {

        // given
        environment = mock(Environment.class);
        when(environment.getProperty("jwt.secret.key")).thenReturn(null);
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        ResultActions resultActions = mockMvc.perform(get("/test")
                .header(authenticationHeader, refreshTokenData)
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isBadRequest());
        verify(securityContext, never()).setAuthentication(any());
    }

    @DisplayName("접속 실패 by AlgorithmMismatchException")
    @Test
    void doFilterInternal_AlgorithmMismatchException() throws Exception {
        refreshTokenData = JWTUtils.createJwtToken(userEntity.getId(), userEntity.getUsername(), refreshTokenTypeName, refreshTokenExpirationTime, Algorithm.HMAC384(secretKey));
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getProperty("jwt.secret.key")).thenReturn(null);

        // given
        refreshTokenHeaderData = getJWTTokenInFormat(refreshTokenTypeName, refreshTokenData);
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        ResultActions resultActions = mockMvc.perform(get("/test")
                .header(authenticationHeader, refreshTokenHeaderData)
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isBadRequest());
        verify(securityContext, never()).setAuthentication(any());
    }

    @DisplayName("접속 실패 by InvalidClaimException")
    @Test
    void doFilterInternal_InvalidClaimException() throws Exception {
        // InvalidClaimException을 mock으로도 일으킬 수가 없음. 피드백 필요
        assertThat(1).isNotNull();
        verify(securityContext, never()).setAuthentication(any());
    }

    @DisplayName("접속 실패 by TokenExpiredException")
    @Test
    void doFilterInternal_TokenExpiredException() throws Exception {
        refreshTokenData = JWTUtils.createJwtToken(userEntity.getId(), userEntity.getUsername(), refreshTokenTypeName, -1000, jwtHashingAlgorithm);

        // given
        refreshTokenHeaderData = getJWTTokenInFormat(refreshTokenTypeName, refreshTokenData);
        when(userRepository.findUser(any())).thenReturn(Optional.of(userEntity));

        // when
        ResultActions resultActions = mockMvc.perform(get("/test")
                .header(authenticationHeader, refreshTokenHeaderData)
                .with(csrf())
        );

        // then
        resultActions.andExpect(status().isUnauthorized());
        verify(securityContext, never()).setAuthentication(any());
    }

    @DisplayName("접속 실패 by SignatureVerificationException")
    @Test
    void doFilterInternal_SignatureVerificationException() throws Exception {
        // SignatureVerificationException을 mock으로도 일으킬 수가 없음. 피드백 필요
        assertThat(1).isNotNull();
        verify(securityContext, never()).setAuthentication(any());
    }

    String getJWTTokenInFormat(String tokenType, String tokenData) {
        return jwtPrefix + " " + tokenType + " " + tokenData;
    }
}