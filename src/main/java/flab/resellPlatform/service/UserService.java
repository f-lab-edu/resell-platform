package flab.resellPlatform.service;

import flab.resellPlatform.domain.User;
import flab.resellPlatform.domain.UserDTO;
import flab.resellPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public User createAccount(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        return userRepository.save(user);
    }

    public Optional<User> viewAccount(Long id) {
        return userRepository.findById(id);
    }

    public User updateAccount(Long id, UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        return userRepository.update(id, user);
    }

    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password));
    }

}
