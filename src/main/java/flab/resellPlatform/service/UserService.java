package flab.resellPlatform.service;

import flab.resellPlatform.domain.User;
import flab.resellPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long createAccount(User user) {
        userRepository.save(user);
        return user.getId();
    }

    public User viewAccount(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateAccount(Long id, User user) {
        return userRepository.update(id, user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElse(null);
    }

}
