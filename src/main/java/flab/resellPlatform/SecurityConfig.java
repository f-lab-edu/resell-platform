package flab.resellPlatform;

import flab.resellPlatform.common.jwt.JwtAuthenticationFilter;
import flab.resellPlatform.common.jwt.JwtAuthorizationFilter;
import flab.resellPlatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // UserRepository는 service layer에서만 접근해야하지만, 이 필터가 요구하는 기능이 #27 브랜치에 있음.
    // 해당 브랜치가 머지되면 UserService로 교체 예정.
    private final UserRepository userRepository;
    private final Environment environment;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 x
                .and()
                    .formLogin().disable()  // Spring security에서 기본적으로 제공하는 폼 로그인 사용x
                    .httpBasic().disable()  // username과 pw 들고 가는 basic 방식 사용x, bearer 방식 사용할 예정.

                    .addFilter(new JwtAuthenticationFilter(authenticationManagerBean(), environment))
                    .addFilter(jwtAuthorizationFilter())
                    .authorizeRequests()
                    .anyRequest().permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(authenticationManagerBean(), userRepository, environment);
    }
}