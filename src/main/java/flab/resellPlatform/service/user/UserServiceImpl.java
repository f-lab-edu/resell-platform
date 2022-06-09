package flab.resellPlatform.service.user;

import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public String join(UserDTO userInfo) {
        UserEntity userEntity = modelMapper.map(userInfo, UserEntity.class);
        if (checkIfUserNameDuplication(userEntity))
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        else
            userRepository.save(userEntity);
        return null;
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
