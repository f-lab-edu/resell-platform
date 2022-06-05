package flab.resellPlatform.user.repository;

import flab.resellPlatform.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisUserRepository implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        System.out.println("MybatisUserRepository");
        userMapper.save(user);
        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        return userMapper.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

}
