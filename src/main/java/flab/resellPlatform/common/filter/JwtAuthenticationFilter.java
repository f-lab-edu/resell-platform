package flab.resellPlatform.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.form.LoginForm;
import flab.resellPlatform.common.response.jwt.Token;
import flab.resellPlatform.common.response.jwt.TokenResponse;
import flab.resellPlatform.common.util.JwtUtil;
import flab.resellPlatform.common.util.ResponseCreator;
import flab.resellPlatform.domain.UserDetailsImpl;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
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
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper om;
    private final Environment env;
    private final RedisTemplate<String, String> redisTemplate;
    private final ResponseCreator responseCreator;

    private static String typeAccess;
    private static String typeRefresh;
    private static String typeAccessExp;
    private static String typeRefreshExp;
    private static String secretKey;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper om, Environment env, RedisTemplate<String, String> redisTemplate, ResponseCreator responseCreator) {
        this.authenticationManager = authenticationManager;
        this.om = om;
        this.env = env;
        this.redisTemplate = redisTemplate;
        this.responseCreator = responseCreator;

        typeAccess = env.getProperty("jwt.type.access");
        typeRefresh = env.getProperty("jwt.type.refresh");
        typeAccessExp = env.getProperty("jwt.access.expiration");
        typeRefreshExp = env.getProperty("jwt.access.expiration");
        secretKey = env.getProperty("jwt.secret");
    }

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

        Date accessTokenExp = JwtUtil.generateExpDate(typeAccessExp);
        Date refreshTokenExp = JwtUtil.generateExpDate(typeRefreshExp);

        String accessToken = JwtUtil.createToken(
                typeAccess,
                accessTokenExp,
                claims,
                SignatureAlgorithm.HS512, secretKey
        );

        String refreshToken = JwtUtil.createToken(
                typeRefresh,
                refreshTokenExp,
                claims,
                SignatureAlgorithm.HS512, secretKey
        );

        TokenResponse tokenResponse = new TokenResponse(new Token(accessToken, accessTokenExp), new Token(refreshToken, refreshTokenExp));

        responseCreator.createBody(
                response,
                HttpStatus.OK,
                "login.success",
                tokenResponse
        );

        redisTemplate.opsForValue().set(userDetails.getId().toString(), refreshToken, Long.parseLong(typeRefreshExp), TimeUnit.MILLISECONDS);
    }

}
