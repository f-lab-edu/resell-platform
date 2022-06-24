package flab.resellPlatform.repository.user;

import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.PasswordInquiryForm;
import flab.resellPlatform.domain.user.StrictLoginInfo;
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
    public Optional<String> findUsername(String phoneNumber) {
        return userMapper.findUsername(phoneNumber);
    }

    @Override
    public List<UserEntity> findAll() {
        return userMapper.findAll();
    }

    @Override
    public int updatePassword(LoginInfo loginInfo) {
        return userMapper.updatePassword(loginInfo);
    }

    @Override
    public int updatePassword(StrictLoginInfo strictLoginInfo) {
        return userMapper.updatePasswordSecurely(strictLoginInfo);
    }

}
