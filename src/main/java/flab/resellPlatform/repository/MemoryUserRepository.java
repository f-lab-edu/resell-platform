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
    public User update(Long id, User user) {
        user.setId(id);
        storage.replace(id, user);
        return user;
    }

    @Override
    public User findById(Long id) {
        return storage.get(id);
    }

}
