package flab.resellPlatform.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    // UserRepository는 service layer에서만 접근해야하지만, 이 필터가 요구하는 기능이 #27 브랜치에 있음.
    // 해당 브랜치가 머지되면 UserService로 교체 예정.
    private final UserRepository userRepository;
    private final Environment environment;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, Environment environment) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        // jwt 설정 정보 불러오기
        String jwtSecretKey = environment.getProperty("jwt.secret.key");
        String jwtHeaderName = environment.getProperty("jwt.header.name");
        String jwtPrefix = environment.getProperty("jwt.prefix");

        // jwt 토큰 방식으로 로그인 시도하는지 확인
        String jwtHeader = request.getHeader(jwtHeaderName);
        if (jwtHeader == null || !jwtHeader.startsWith(jwtPrefix)) {
            chain.doFilter(request, response);
            return;
        }

        // JWT 토큰을 이용해서 정상적인 사용자인지 확인
        String jwtToken = jwtHeader.replace(jwtPrefix + " ", "");
        String username = JWT.require(Algorithm.HMAC512(jwtSecretKey)).build().verify(jwtToken).getClaim("username").asString();

        // 서명이 정상적으로 됨
        if (username != null) {
            Optional<UserEntity> userEntity = userRepository.findUser(username);
            PrincipleDetails principleDetails = new PrincipleDetails(userEntity.get());
            
            // 인증 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principleDetails,
                    null,
                    principleDetails.getAuthorities());

            // Spring security의 권한 관리 기능을 사용하기 위해 security의 세션에 접근하여 Authentication 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
