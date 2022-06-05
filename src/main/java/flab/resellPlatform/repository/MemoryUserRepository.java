package flab.resellPlatform.repository;

import flab.resellPlatform.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryUserRepository implements UserRepository {

    private static Map<Long, User> storage = new ConcurrentHashMap<>();
    private static long identifier = 0L;

    @Override
    public User save(User user) {
        user.setId(++identifier);
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long id, User updatedUser) {
        User user = findById(id);
        user.setUsername(updatedUser.getUsername());
        user.setPassword(updatedUser.getPassword());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setName(updatedUser.getName());
        user.setNickname(updatedUser.getNickname());
        user.setEmail(updatedUser.getEmail());
        user.setShoeSize(updatedUser.getShoeSize());
        return user;
    }

    @Override
    public User findById(Long id) {
        return storage.get(id);
    }

}
