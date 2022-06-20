package flab.resellPlatform.service;

import flab.resellPlatform.common.exception.DuplicateUsernameException;
import flab.resellPlatform.common.exception.UserNotFoundException;
import flab.resellPlatform.domain.User;
import flab.resellPlatform.domain.UserDTO;
import flab.resellPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public User createAccount(UserDTO userDTO) throws DuplicateUsernameException {
        Optional<User> user = userRepository.findByUsername(userDTO.getUsername());
        if (user.isPresent()) throw new DuplicateUsernameException();

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);

        User userEntity = modelMapper.map(userDTO, User.class);
        return userRepository.save(userEntity);
    }

    public Optional<User> viewAccount(Long id) {
        return userRepository.findById(id);
    }

    public User updateAccount(Long id, UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        return userRepository.update(id, user);
    }

}
