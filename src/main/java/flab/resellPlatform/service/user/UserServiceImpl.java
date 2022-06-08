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
//        System.out.println("in UserServiceImpl");
        UserEntity userEntity = modelMapper.map(userInfo, UserEntity.class);
        userRepository.save(userEntity);
        return null;
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
