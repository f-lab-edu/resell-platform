package flab.resellPlatform.common.filter;

import com.auth0.jwt.algorithms.Algorithm;
import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;

public class AccessJWTAuthorizationFilter extends AbstractJWTAuthorizationFilter{

    private final ApplicationContext applicationContext;

    public AccessJWTAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, Environment environment, MessageSourceAccessor messageSourceAccessor, Algorithm jwtHashingAlgorithm, ApplicationContext applicationContext) {
        super(authenticationManager, userRepository, environment, messageSourceAccessor, jwtHashingAlgorithm);
        this.applicationContext = applicationContext;
    }

    @Override
    protected boolean checkTokenType(String tokenType) {
        return tokenType.equals(environment.getProperty("jwt.token.type.access"));
    }

    @Override
    protected boolean postProcessAfterAuthentication(HttpServletResponse response, FilterChain chain, UserEntity userEntity, String tokenData) {
        storeAuthenticationForAuthorization(userEntity);
        return true;
    }

    private void storeAuthenticationForAuthorization(UserEntity userEntity) {
        PrincipleDetails principleDetails = new PrincipleDetails(userEntity);

        // 인증 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principleDetails,
                null,
                principleDetails.getAuthorities());

        // Spring security의 권한 관리 기능을 사용하기 위해 security의 세션에 접근하여 Authentication 객체 저장
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityContext securityContext = (SecurityContext) applicationContext.getBean("securityContext");
        securityContext.setAuthentication(authentication);
    }
}
