package flab.resellPlatform.service.user;

import flab.resellPlatform.domain.user.*;
import flab.resellPlatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Optional<UserDTO> createUser(UserDTO userInfo) {
        UserEntity userEntity = modelMapper.map(userInfo, UserEntity.class);
        userRepository.save(userEntity);
        return Optional.of(userInfo);
    }

    @Override
    public Optional<String> findUsername(String phoneNumber) {
        return userRepository.findUsername(phoneNumber);
    }

    @Override
    public int updatePassword(LoginInfo loginInfo) {
        return userRepository.updatePassword(loginInfo);
    }

    @Override
    public int updatePassword(StrictLoginInfo strictLoginInfo) {
        return userRepository.updatePassword(strictLoginInfo);
    }
}
