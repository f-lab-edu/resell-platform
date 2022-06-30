package flab.resellPlatform.common.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.ThreadLocalStandardResponseBucketHolder;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    // UserRepository는 service layer에서만 접근해야하지만, 이 필터가 요구하는 기능이 #27 브랜치에 있음.
    // 해당 브랜치가 머지되면 UserService로 교체 예정.
    private final UserRepository userRepository;
    private final Environment environment;
    private final MessageSourceAccessor messageSourceAccessor;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, Environment environment, MessageSourceAccessor messageSourceAccessor) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.environment = environment;
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        // jwt 설정 정보 불러오기
        String jwtSecretKey = environment.getProperty("jwt.secret.key");
        String jwtHeaderName = environment.getProperty("jwt.header.name");
        String jwtPrefix = environment.getProperty("jwt.prefix");
        String refreshTokenKey = environment.getProperty("jwt.refresh.key");

        // jwt 토큰 방식으로 로그인 시도하는지 확인
        String jwtRawData = request.getHeader(jwtHeaderName);
        if (jwtRawData == null) {
            chain.doFilter(request, response);
            return;
        }
        String[] jwtContents = jwtRawData.split(" ");
        String token_type = null;
        String token_data = null;
        try {
            token_type = jwtContents[1];
            token_data = jwtContents[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            // 예외 처리 예정
            chain.doFilter(request, response);
            return;
        }

        if (token_type.equals(environment.getProperty("jwt.token_type.access"))) {
            String username = null;
            try {
                username = JWT.require(Algorithm.HMAC512(jwtSecretKey)).build().verify(token_data).getClaim("username").asString();

            } catch (TokenExpiredException e) {
                ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                        .setMessage(messageSourceAccessor.getMessage("jwt.access.token.expired"));
                return;
            }

            // 서명이 정상적으로 됨
            if (checkTokenIfVerified(username)) {
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
        }
        chain.doFilter(request, response);
    }

    private boolean checkTokenIfVerified(String username) {
        return username != null;
    }
}
