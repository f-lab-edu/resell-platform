package flab.resellPlatform.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.util.PropertyUtil;
import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
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
            PrincipleDetails principleDetails = (PrincipleDetails) authentication.getPrincipal();
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

        long jwtExpirationTime = Long.parseLong(PropertyUtil.getProperty("jwt.expiration.time"));
        String jwtSecretKey = PropertyUtil.getProperty("jwt.secret.key");
        String jwtHeaderName = PropertyUtil.getProperty("jwt.header.name");
        String jwtPrefix = PropertyUtil.getProperty("jwt.prefix");

        String jwtToken = JWT.create()
                .withSubject("jwtToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .withClaim("id", principleDetails.getUser().getId())
                .withClaim("username", principleDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(jwtSecretKey));

        response.addHeader(jwtHeaderName, jwtPrefix + " " + jwtToken);
    }
}
