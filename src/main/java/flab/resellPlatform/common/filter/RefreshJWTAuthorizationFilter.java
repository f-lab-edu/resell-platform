package flab.resellPlatform.common.filter;

import com.auth0.jwt.algorithms.Algorithm;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.common.utils.JWTUtils;
import flab.resellPlatform.common.utils.ResponseUtils;
import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class RefreshJWTAuthorizationFilter extends AbstractJWTAuthorizationFilter {

    protected RedisTemplate<String, Object> redisSessionTemplate;
    private final AuthenticationStoreProxy authenticationStoreProxy;

    public RefreshJWTAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, Environment environment, MessageSourceAccessor messageSourceAccessor, Algorithm jwtHashingAlgorithm, RedisTemplate<String, Object> redisSessionTemplate, AuthenticationStoreProxy authenticationStoreProxy) {
        super(authenticationManager, userRepository, environment, messageSourceAccessor, jwtHashingAlgorithm);
        this.redisSessionTemplate = redisSessionTemplate;
        this.authenticationStoreProxy = authenticationStoreProxy;
    }

    @Override
    protected boolean checkTokenType(String tokenType) {
        return tokenType.equals(environment.getProperty("jwt.token.type.refresh"));
    }

    @Override
    protected boolean postProcessAfterAuthentication(HttpServletResponse response, FilterChain chain, UserEntity userEntity, String tokenData) {
        // 기존 refresh token과 비교
        Object storedRefreshToken = redisSessionTemplate.opsForValue().get(environment.getProperty("jwt.token.type.refresh") + String.valueOf(userEntity.getId()));
//
//        if (storedRefreshToken != null && !storedRefreshToken.equals(tokenData)) {
//            JWTUtils.returnErrorCodeWithHeader(response, messageSourceAccessor.getMessage("jwt.invalid.token"), HttpStatus.UNAUTHORIZED);
//            return false;
//        }
//
//        // 새로운 refresh token 발급
//        String accessToken = JWTUtils.createJwtToken(userEntity.getId(), userEntity.getUsername(), environment.getProperty("jwt.token.type.access"), Long.parseLong(environment.getProperty("jwt.access.expiration.time")), jwtHashingAlgorithm);
//        String refreshToken = JWTUtils.createJwtToken(userEntity.getId(), userEntity.getUsername(), environment.getProperty("jwt.token.type.refresh"), Long.parseLong(environment.getProperty("jwt.refresh.expiration.time")), jwtHashingAlgorithm);
//        // redis 내 refresh token을 새 refresh token으로 대체
//        // 새로운 Access token과 refresh token을 body에 삽입
//        redisSessionTemplate.opsForValue().set(
//                environment.getProperty("jwt.token.type.refresh") + String.valueOf(userEntity.getId()),
//                refreshToken,
//                Long.parseLong(environment.getProperty("jwt.refresh.expiration.time")),
//                TimeUnit.MILLISECONDS);

//        // 응답 생성
//        StandardResponse standardResponse = StandardResponse.builder()
//                .message(messageSourceAccessor.getMessage("common.login.succeeded"))
//                .data(Map.of(
//                        environment.getProperty("jwt.token.type.access"), accessToken,
//                        environment.getProperty("jwt.token.type.key"), environment.getProperty("jwt.prefix"),
//                        environment.getProperty("jwt.expiration.time.key"), environment.getProperty("jwt.access.expiration.time"),
//                        environment.getProperty("jwt.token.type.refresh"), refreshToken
//                ))
//                .build();
//
//        ResponseUtils.createCustomMessage(response, HttpStatus.OK, standardResponse);

        storeAuthenticationForAuthorization(userEntity);
        return true;
    }

    private void storeAuthenticationForAuthorization(UserEntity userEntity) {
        PrincipleDetails principleDetails = new PrincipleDetails(userEntity);

        // 인증 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principleDetails,
                null,
                principleDetails.getAuthorities());

        // Spring security의 권한 관리 기능을 사용하기 위해 security의 세션에 접근하여 Authentication 객체 저장
        authenticationStoreProxy.storeAuthentication(authentication);
    }
}
