package flab.resellPlatform.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.form.LoginForm;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.common.response.jwt.Token;
import flab.resellPlatform.common.response.jwt.TokenResponse;
import flab.resellPlatform.common.util.JwtUtil;
import flab.resellPlatform.common.util.MessageUtil;
import flab.resellPlatform.domain.UserDetailsImpl;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper om;
    private final Environment env;
    private final RedisTemplate<String, String> redisTemplate;
    private final MessageUtil messageUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginForm loginForm = om.readValue(request.getInputStream(), LoginForm.class);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword());
            Authentication authentication = authenticationManager.authenticate(token);

            return authentication;
        } catch (IOException e) {
            log.error("IOException from method attemptAuthentication(): {}", e.getMessage());
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());

        String TYPE_ACCESS = env.getProperty("jwt.type.access");
        String TYPE_REFRESH = env.getProperty("jwt.type.refresh");
        String ACCESS_TOKEN_EXP = env.getProperty("jwt.access.expiration");
        String REFRESH_TOKEN_EXP = env.getProperty("jwt.refresh.expiration");
        String SECRET_KEY = env.getProperty("jwt.secret");

        Date accessTokenExp = JwtUtil.generateExpDate(ACCESS_TOKEN_EXP);
        Date refreshTokenExp = JwtUtil.generateExpDate(REFRESH_TOKEN_EXP);

        String accessToken = JwtUtil.createToken(
                TYPE_ACCESS,
                accessTokenExp,
                claims,
                SignatureAlgorithm.HS512, SECRET_KEY
        );

        String refreshToken = JwtUtil.createToken(
                TYPE_REFRESH,
                refreshTokenExp,
                claims,
                SignatureAlgorithm.HS512, SECRET_KEY
        );

        TokenResponse tokenResponse = new TokenResponse(new Token(accessToken, accessTokenExp), new Token(refreshToken, refreshTokenExp));
        StandardResponse standardResponse = new StandardResponse(messageUtil.getMessage("login.success"), tokenResponse);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(om.writeValueAsString(standardResponse));

        redisTemplate.opsForValue().set(userDetails.getId().toString(), refreshToken, Long.parseLong(REFRESH_TOKEN_EXP), TimeUnit.MILLISECONDS);
    }

}
