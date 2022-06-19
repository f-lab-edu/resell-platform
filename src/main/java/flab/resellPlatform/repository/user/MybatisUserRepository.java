package flab.resellPlatform.repository.user;

import flab.resellPlatform.domain.user.PasswordInquiryForm;
import flab.resellPlatform.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisUserRepository implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public UserEntity save(UserEntity userEntity) {
        userMapper.save(userEntity);
        return userEntity;
    }

    @Override
    public Optional<UserEntity> findUser(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public List<UserEntity> findAll() {
        return userMapper.findAll();
    }

}
