package flab.resellPlatform.service;

import flab.resellPlatform.domain.User;
import flab.resellPlatform.domain.UserDTO;
import flab.resellPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

    public User viewAccount(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateAccount(Long id, UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        return userRepository.update(id, user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElse(null);
    }

}
