package flab.resellPlatform.common.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.ThreadLocalStandardResponseBucketHolder;
import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
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
import java.util.Date;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final Environment environment;
    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 1. username, password 받아서
        try {
            ObjectMapper om = new ObjectMapper();
            UserEntity userEntity = om.readValue(request.getInputStream(), UserEntity.class);
            // 2. 정상인지 시도. authenticationManager로 로그인 시도를 하면!
            // PrincipleDetailService가 호출, loadUserByUsername() 함수 실행됨.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
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

        String accessToken = createJwtToken(principleDetails, "accessToken", Long.parseLong(environment.getProperty("jwt.access.expiration.time")));
        String refreshToken = createJwtToken(principleDetails, "refreshToken", Long.parseLong(environment.getProperty("jwt.refresh.expiration.time")));

        ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                        .setMessage(messageSourceAccessor.getMessage("common.login.succeeded"));

        ThreadLocalStandardResponseBucketHolder.setResponseData(environment.getProperty("jwt.token_type.access"), accessToken);
        ThreadLocalStandardResponseBucketHolder.setResponseData("token_type", environment.getProperty("jwt.prefix"));
        ThreadLocalStandardResponseBucketHolder.setResponseData("expires_in", environment.getProperty("jwt.access.expiration.time"));
        ThreadLocalStandardResponseBucketHolder.setResponseData(environment.getProperty("jwt.token_type.refresh"), refreshToken);
        // refresh token을 StandardResponse의 data 영역에 저장해야함.
    }

    private String createJwtToken(PrincipleDetails principleDetails, String subject, long expirationTime) {
        String jwtToken = JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withClaim("id", principleDetails.getUser().getId())
                .withClaim("username", principleDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(environment.getProperty("jwt.secret.key")));
        return jwtToken;
    }
}
