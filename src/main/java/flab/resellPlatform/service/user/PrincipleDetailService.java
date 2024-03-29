package flab.resellPlatform.service.user;

import flab.resellPlatform.domain.user.PrincipleDetails;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipleDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findUser(username);
        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException(messageSourceAccessor.getMessage("user.username.notFound"));
        }
        return new PrincipleDetails(userEntity.get());
    }
}
