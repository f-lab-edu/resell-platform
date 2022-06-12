package flab.resellPlatform.service.user;

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
    public Optional<UserDTO> join(UserDTO userInfo) {
        UserEntity userEntity = modelMapper.map(userInfo, UserEntity.class);
        if (checkIfUserNameDuplication(userEntity)) {
            return Optional.empty();
        } else {
            userRepository.save(userEntity);
        }
        return Optional.of(userInfo);
    }

    public boolean checkIfUserNameDuplication(UserEntity userEntity) {
        int userNameCount = userRepository.getUsernameCount(userEntity.getUsername());
        return userNameCount >= 1 ? true : false;
    }

    @Override
    public String findId(UserDTO userInfo) {
        return null;
    }

    @Override
    public String findPassword(UserDTO userInfo) {
        return null;
    }
}
