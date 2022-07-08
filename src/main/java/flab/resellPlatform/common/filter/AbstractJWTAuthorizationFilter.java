package flab.resellPlatform.common.filter;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import flab.resellPlatform.common.utils.JWTUtils;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

abstract public class AbstractJWTAuthorizationFilter extends BasicAuthenticationFilter {
    // UserRepository는 service layer에서만 접근해야하지만, 이 필터가 요구하는 기능이 #27 브랜치에 있음.
    // 해당 브랜치가 머지되면 UserService로 교체 예정.
    protected final UserRepository userRepository;
    protected final Environment environment;
    protected final MessageSourceAccessor messageSourceAccessor;
    protected Algorithm jwtHashingAlgorithm;


    public AbstractJWTAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, Environment environment, MessageSourceAccessor messageSourceAccessor, Algorithm jwtHashingAlgorithm) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.environment = environment;
        this.messageSourceAccessor = messageSourceAccessor;
        this.jwtHashingAlgorithm = jwtHashingAlgorithm;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        // jwt 설정 정보 불러오기
        String jwtSecretKey = environment.getProperty("jwt.secret.key");
        String jwtHeaderName = environment.getProperty("jwt.header.name");
        String jwtPrefix = environment.getProperty("jwt.prefix");

        // JWT 방식으로 로그인 시도하는지 확인
        String jwtRawData = request.getHeader(jwtHeaderName);
        if (jwtRawData == null) {
            chain.doFilter(request, response);
            return;
        }

        // 형식이 올바른지 검증
        String tokenType = null;
        String tokenData = null;
        try {
            tokenType = JWTUtils.getTokenType(jwtRawData);
            tokenData = JWTUtils.getTokenData(jwtRawData);
        } catch (ArrayIndexOutOfBoundsException e) {
            JWTUtils.returnErrorCodeWithHeader(response, messageSourceAccessor.getMessage("jwt.invalid.request"), HttpStatus.BAD_REQUEST);
            return;
        }

        // 원하는 토큰 타입인지 검증
        if (checkTokenType(tokenType)) {
            System.out.println("1");

            // 변조 및 만료 검증
            String username = null;
            try {
                System.out.println("2");
                username = JWTUtils.getClaimWithVerificationProcess(tokenData, jwtSecretKey, environment.getProperty("jwt.claim.username"));
            } catch (IllegalArgumentException | AlgorithmMismatchException | InvalidClaimException e) {
                JWTUtils.returnErrorCodeWithHeader(response, messageSourceAccessor.getMessage("jwt.invalid.request"), HttpStatus.BAD_REQUEST);
                System.out.println("3.5");
                return;
            } catch (TokenExpiredException | SignatureVerificationException e) {
                System.out.println("3");
                JWTUtils.returnErrorCodeWithHeader(response, messageSourceAccessor.getMessage("jwt.invalid.token"), HttpStatus.UNAUTHORIZED);
                return;
            }
            System.out.println("4");
            Optional<UserEntity> userEntity = userRepository.findUser(username);
            if (!postProcessAfterAuthentication(response, chain, userEntity.get(), tokenData))
                return;

        }

        chain.doFilter(request, response);
    }

    /**
     * 토큰의 타입을 판별하는 함수.
     *
     * @return boolean
     *
     * @작성일 7/8/2022
     * @작성자 minsuk
     */
    abstract protected boolean checkTokenType(String tokenType);

    /**
    * postProcessAfterAuthentication 인증 성공 후 진행할 작업.
    *
    * @return True(체인 계속 진행), False(체인 징행 정지)
    *
    * @작성일 7/8/2022
    * @작성자 minsuk
    */
    abstract protected boolean postProcessAfterAuthentication(HttpServletResponse response, FilterChain chain, UserEntity userEntity, String tokenData);
}
