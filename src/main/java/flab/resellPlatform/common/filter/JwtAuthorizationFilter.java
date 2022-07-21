package flab.resellPlatform.common.filter;

import flab.resellPlatform.common.exception.UserNotFoundException;
import flab.resellPlatform.common.response.jwt.Token;
import flab.resellPlatform.common.response.jwt.TokenResponse;
import flab.resellPlatform.common.util.JwtUtil;
import flab.resellPlatform.common.util.ResponseCreator;
import flab.resellPlatform.domain.User;
import flab.resellPlatform.domain.UserDetailsImpl;
import flab.resellPlatform.repository.UserRepository;
import io.jsonwebtoken.*;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
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
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final Environment env;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ResponseCreator responseCreator;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, Environment env, UserRepository userRepository, RedisTemplate<String, String> redisTemplate, ResponseCreator responseCreator) {
        super(authenticationManager);
        this.env = env;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.responseCreator = responseCreator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        String[] jwtData = authHeader.split(" ");

        if (jwtData.length != 3 || !hasValidPrefix(jwtData[0]) || !hasValidTokenType(jwtData[1])) {
            /*
             1. Prefix + Token Type + Token의 3단 구조가 아닌 경우
             2. Prefix가 Bearer가 아닌 경우
             3. Token Type이 Access 또는 Refresh가 아닌 경우
             */
            responseCreator.createBody(
                    response,
                    HttpStatus.BAD_REQUEST,
                    "jwt.invalid.format",
                    null
            );

            return;
        }

        String username = validateToken(jwtData[2], response);

        if (username == null) return;

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException());

        if (jwtData[1].equals(env.getProperty("jwt.type.access"))) {
            processAccessToken(user);
            chain.doFilter(request, response);
        }

        if (jwtData[1].equals(env.getProperty("jwt.type.refresh"))) {
            processRefreshToken(user, jwtData[2], response);
        }
    }

    public boolean hasValidTokenType(String jwtType) {
        String TYPE_ACCESS = env.getProperty("jwt.type.access");
        String TYPE_REFRESH = env.getProperty("jwt.type.refresh");

        return jwtType.equals(TYPE_ACCESS) || jwtType.equals(TYPE_REFRESH);
    }

    public boolean hasValidPrefix(String jwtPrefix) {
        return jwtPrefix.equals("Bearer");
    }

    public String validateToken(String jwt, HttpServletResponse response) {
        String SECRET_KEY = env.getProperty("jwt.secret");
        String username = null;

        try {
            username = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwt).getBody().get("username").toString();
            return username;
        } catch (Exception e) {
            // UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException
            responseCreator.createHeader(
                    response,
                    HttpStatus.BAD_REQUEST,
                    HttpHeaders.WWW_AUTHENTICATE,
                    "jwt.invalid.token"
            );
        }

        return null;
    }

    public void processAccessToken(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void processRefreshToken(User user, String token, HttpServletResponse response) throws IOException {
        Long userId = user.getId();
        String prevRefreshToken = redisTemplate.opsForValue().get(userId.toString());

        if (prevRefreshToken != null && !prevRefreshToken.equals(token)) {
            responseCreator.createHeader(
                    response,
                    HttpStatus.BAD_REQUEST,
                    HttpHeaders.WWW_AUTHENTICATE,
                    "jwt.invalid.token"
            );
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());

        String TYPE_ACCESS = env.getProperty("jwt.type.access");
        String TYPE_REFRESH = env.getProperty("jwt.type.refresh");
        String ACCESS_TOKEN_EXP = env.getProperty("jwt.access.expiration");
        String REFRESH_TOKEN_EXP = env.getProperty("jwt.access.expiration");
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

        responseCreator.createBody(
                response,
                HttpStatus.OK,
                "jwt.refresh.success",
                tokenResponse
        );

        redisTemplate.opsForValue().set(user.getId().toString(), refreshToken, Long.parseLong(REFRESH_TOKEN_EXP), TimeUnit.MILLISECONDS);
    }
}
