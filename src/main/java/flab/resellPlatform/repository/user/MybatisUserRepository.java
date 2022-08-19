package flab.resellPlatform.repository.user;

import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.StrictLoginInfo;
import flab.resellPlatform.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisUserRepository implements UserRepository {

    private final UserMapper userMapper;
    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    public UserEntity save(UserEntity userEntity) throws DuplicateKeyException {
        try {
            userMapper.save(userEntity);

        } catch (SQLException e) {
            throw new DuplicateKeyException(messageSourceAccessor.getMessage("user.username.duplicated"), e);
        }
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
