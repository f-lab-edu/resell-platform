package flab.resellPlatform.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import flab.resellPlatform.service.user.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

// Security가 가지고 있는 필터 중에 BasicAuthenticationFilter가 존재함.
// 권한이나 인증이 필요한 특정 주소를 요청했을 때, 위 필터를 무조건 타게 돼있음.
// 만약에 권한이 인증이 필요한 주소가 아니면, 해당 필터를 안탐.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    // UserRepository는 service layer에서만 접근해야하지만, 이 필터가 요구하는 기능이 #27 브랜치에 있음.
    // 해당 브랜치가 머지되면 UserService로 교체 예정.
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        super.doFilterInternal(request, response, chain);

        // jwt 토큰 방식으로 로그인 시도하는지 확인
        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        // JWT 토큰을 이용해서 정상적인 사용자인지 확인
        String jwtToken = jwtHeader.replace("Bearer ", "");
        String username = JWT.require(Algorithm.HMAC512("mySecretKey")).build().verify(jwtToken).getClaim("username").asString();

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
