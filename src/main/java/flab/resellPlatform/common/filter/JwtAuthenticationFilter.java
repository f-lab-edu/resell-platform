package flab.resellPlatform.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.form.LoginForm;
import flab.resellPlatform.common.util.JwtUtil;
import flab.resellPlatform.domain.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
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
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper om;
    private final Environment env;
    private final RedisTemplate<String, String> redisTemplate;

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
        Map<String, Object> userDetailsMap = om.convertValue(userDetails, Map.class);

        String TYPE_ACCESS = env.getProperty("jwt.type.access");
        String TYPE_REFRESH = env.getProperty("jwt.type.refresh");
        String ACCESS_TOKEN_EXP = env.getProperty("jwt.access.expiration");
        String REFRESH_TOKEN_EXP = env.getProperty("jwt.access.expiration");
        String SECRET_KEY = env.getProperty("jwt.secret");

        String accessToken = JwtUtil.createToken(
                TYPE_ACCESS,
                new Date(System.currentTimeMillis() + Long.parseLong(ACCESS_TOKEN_EXP)),
                userDetailsMap,
                SignatureAlgorithm.HS512, SECRET_KEY
        );

        String refreshToken = JwtUtil.createToken(
                TYPE_REFRESH,
                new Date(System.currentTimeMillis() + Long.parseLong(REFRESH_TOKEN_EXP)),
                userDetailsMap,
                SignatureAlgorithm.HS512, SECRET_KEY
        );

        response.addHeader("Authorization",
                "Bearer " + TYPE_ACCESS + " " + accessToken + " " + TYPE_REFRESH + " " + refreshToken);

        redisTemplate.opsForValue().set(userDetails.getId().toString(), refreshToken, Long.parseLong(REFRESH_TOKEN_EXP));
    }
}
