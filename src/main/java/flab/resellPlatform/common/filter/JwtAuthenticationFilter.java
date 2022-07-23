package flab.resellPlatform.common.filter;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.ThreadLocalStandardResponseBucketHolder;
import flab.resellPlatform.common.utils.JWTUtils;
import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.domain.user.UserEntity;
import lombok.Setter;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Environment environment;
    private final MessageSourceAccessor messageSourceAccessor;
    private final RedisTemplate<String, Object> redisSessionTemplate;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, Environment environment, MessageSourceAccessor messageSourceAccessor, RedisTemplate<String, Object> redisSessionTemplate) {
        super(authenticationManager);
        this.environment = environment;
        this.messageSourceAccessor = messageSourceAccessor;
        this.redisSessionTemplate = redisSessionTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 1. username, password 받아서
        try {
            ObjectMapper om = new ObjectMapper();
            UserEntity userEntity = om.readValue(request.getInputStream(), UserEntity.class);
            // 2. 정상인지 시도. authenticationManager로 로그인 시도를 하면!
            // PrincipleDetailService가 호출, loadUserByUsername() 함수 실행됨.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword());
            Authentication authentication = getAuthenticationManager().authenticate(authenticationToken);
            // 리턴 하는 이유는 권한 관리를 위해서임.
            // JWT 토큰을 사용하면 세션을 사용할 이유가 없지만, 단지 권한 관리를 위해 session을 사용함.
            // Spring security는 session을 사용하여 권한 관리를 하기 때문
            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        PrincipleDetails principleDetails = (PrincipleDetails) authResult.getPrincipal();

        String accessToken = JWTUtils.createJwtToken(
                principleDetails.getUser().getId(),
                principleDetails.getUser().getUsername(),
                environment.getProperty("jwt.token.type.access"),
                Long.parseLong(environment.getProperty("jwt.access.expiration.time")),
                Algorithm.HMAC512(environment.getProperty(("jwt.secret.key"))));
        String refreshToken = JWTUtils.createJwtToken(
                principleDetails.getUser().getId(),
                principleDetails.getUser().getUsername(),
                environment.getProperty("jwt.token.type.refresh"),
                Long.parseLong(environment.getProperty("jwt.refresh.expiration.time")),
                Algorithm.HMAC512(environment.getProperty(("jwt.secret.key"))));

        // redis 내 refresh token을 새 refresh token으로 대체
        // 새로운 Access token과 refresh token을 body에 삽입
       redisSessionTemplate.opsForValue().set(
               environment.getProperty("jwt.token.type.refresh") + String.valueOf(principleDetails.getUser().getId()),
               refreshToken,
               Long.parseLong(environment.getProperty("jwt.refresh.expiration.time")),
               TimeUnit.MILLISECONDS);

        ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                        .setMessage(messageSourceAccessor.getMessage("common.login.succeeded"));

        ThreadLocalStandardResponseBucketHolder.setResponseData(environment.getProperty("jwt.token.type.access"), accessToken);
        ThreadLocalStandardResponseBucketHolder.setResponseData(environment.getProperty("jwt.token.type.key"), environment.getProperty("jwt.prefix"));
        ThreadLocalStandardResponseBucketHolder.setResponseData(environment.getProperty("jwt.expiration.time.key"), environment.getProperty("jwt.access.expiration.time"));
        ThreadLocalStandardResponseBucketHolder.setResponseData(environment.getProperty("jwt.token.type.refresh"), refreshToken);
    }
}
