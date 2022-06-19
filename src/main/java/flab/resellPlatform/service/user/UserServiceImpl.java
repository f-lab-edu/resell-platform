package flab.resellPlatform.service.user;

import flab.resellPlatform.domain.user.PasswordInquiryForm;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
}
