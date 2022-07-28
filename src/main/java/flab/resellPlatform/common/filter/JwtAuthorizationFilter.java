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

    private static String typeAccess;
    private static String typeRefresh;
    private static String typeAccessExp;
    private static String typeRefreshExp;
    private static String secretKey;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, Environment env, UserRepository userRepository, RedisTemplate<String, String> redisTemplate, ResponseCreator responseCreator) {
        super(authenticationManager);
        this.env = env;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.responseCreator = responseCreator;

        typeAccess = env.getProperty("jwt.type.access");
        typeRefresh = env.getProperty("jwt.type.refresh");
        typeAccessExp = env.getProperty("jwt.access.expiration");
        typeRefreshExp = env.getProperty("jwt.access.expiration");
        secretKey = env.getProperty("jwt.secret");
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

    private boolean hasValidTokenType(String jwtType) {
        return jwtType.equals(typeAccess) || jwtType.equals(typeRefresh);
    }

    private boolean hasValidPrefix(String jwtPrefix) {
        return jwtPrefix.equals("Bearer");
    }

    private String validateToken(String jwt, HttpServletResponse response) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody().get("username").toString();
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

    private void processAccessToken(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void processRefreshToken(User user, String token, HttpServletResponse response) throws IOException {
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
                "jwt.refresh.success",
                tokenResponse
        );

        redisTemplate.opsForValue().set(user.getId().toString(), refreshToken, Long.parseLong(typeRefreshExp), TimeUnit.MILLISECONDS);
    }
}
