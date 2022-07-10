package flab.resellPlatform;

import flab.resellPlatform.common.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().disable()
                .csrf().disable()
                .authorizeRequests()
                .mvcMatchers("/", "/users", "/login").permitAll()
                .mvcMatchers("/users/**").hasAnyRole(Role.USER, Role.ADMIN)
                .mvcMatchers("/admin").hasRole(Role.ADMIN);

        httpSecurity
                .formLogin().loginPage("/login")
                .permitAll();

        httpSecurity.httpBasic();

        httpSecurity
                .logout()
                .logoutSuccessUrl("/");
    }

}
