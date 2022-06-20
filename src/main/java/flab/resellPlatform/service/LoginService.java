package flab.resellPlatform.service;

import flab.resellPlatform.common.exception.UserNotFoundException;
import flab.resellPlatform.domain.User;
import flab.resellPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean login(String username, String password) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new UserNotFoundException();

        return passwordEncoder.matches(password, user.get().getPassword());
    }

}
