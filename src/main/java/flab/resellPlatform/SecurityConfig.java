package flab.resellPlatform;

import com.auth0.jwt.algorithms.Algorithm;
import flab.resellPlatform.common.filter.*;
import flab.resellPlatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // UserRepository는 service layer에서만 접근해야하지만, 이 필터가 요구하는 기능이 #27 브랜치에 있음.
    // 해당 브랜치가 머지되면 UserService로 교체 예정.
    private final UserRepository userRepository;
    private final Environment environment;
    private final MessageSourceAccessor messageSourceAccessor;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 x
                .and()
                    .formLogin().disable()  // Spring security에서 기본적으로 제공하는 폼 로그인 사용x
                    .httpBasic().disable()  // username과 pw 들고 가는 basic 방식 사용x, bearer 방식 사용할 예정.

                    .addFilter(jwtAuthenticationFilter())
                    .addFilter(accessJWTAuthorizationFilter())
                    .addFilter(refreshJWTAuthorizationFilter())
                    .authorizeRequests()
                    .anyRequest().permitAll();
    }



//    @Bean(name="authenticationManager")
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManagerBean(), environment, messageSourceAccessor, redisTemplate);
    }

    @Bean
    public AbstractJWTAuthorizationFilter accessJWTAuthorizationFilter() throws Exception {
        return new AccessJWTAuthorizationFilter(authenticationManagerBean(), userRepository, environment, messageSourceAccessor, jwtHashingAlgorithm());
    }

    @Bean
    public AbstractJWTAuthorizationFilter refreshJWTAuthorizationFilter() throws Exception {
        return new RefreshJWTAuthorizationFilter(authenticationManagerBean(), userRepository, environment, messageSourceAccessor, jwtHashingAlgorithm(), redisTemplate);
    }

    @Bean
    public RandomValueStringGenerator randomValueStringGenerator() {
        return new RandomValueStringGenerator();
    }

    @Bean
    public Algorithm jwtHashingAlgorithm() {
        return Algorithm.HMAC512(environment.getProperty("jwt.secret.key"));
    }
}