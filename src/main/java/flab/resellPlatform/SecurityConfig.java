package flab.resellPlatform;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.Role;
import flab.resellPlatform.common.filter.JwtAuthenticationFilter;
import flab.resellPlatform.common.filter.JwtAuthorizationFilter;
import flab.resellPlatform.common.util.MessageUtil;
import flab.resellPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final Environment env;
    private final RedisTemplate<String, String> redisTemplate;
    private final MessageUtil messageUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), objectMapper, env, redisTemplate, messageUtil))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), env, userRepository, messageUtil, objectMapper, redisTemplate))
                .authorizeRequests()
                .antMatchers("/", "/users", "/login").permitAll()
                .antMatchers("/users/**").hasAnyRole(Role.USER, Role.ADMIN)
                .antMatchers("/admin").hasRole(Role.ADMIN)
                .anyRequest().permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
