package flab.resellPlatform.repository;

import flab.resellPlatform.common.exception.DuplicateUsernameException;
import flab.resellPlatform.common.mapper.UserMapper;
import flab.resellPlatform.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MySqlUserRepository implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public User save(User user) throws DuplicateUsernameException {
        try {
            userMapper.save(user);
        } catch (DuplicateKeyException e) {
            throw new DuplicateUsernameException();
        }
        return user;
    }

    @Override
    public User update(Long id, User user) {
        userMapper.update(id, user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

}
