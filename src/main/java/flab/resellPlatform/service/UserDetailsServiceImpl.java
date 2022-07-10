package flab.resellPlatform.service;

import flab.resellPlatform.domain.User;
import flab.resellPlatform.domain.UserDetailsImpl;
import flab.resellPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl();

        userDetailsImpl.setUsername(username);
        userDetailsImpl.setPassword(user.getPassword());

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        userDetailsImpl.setAuthorities(authorities);

        return userDetailsImpl;
    }

}
