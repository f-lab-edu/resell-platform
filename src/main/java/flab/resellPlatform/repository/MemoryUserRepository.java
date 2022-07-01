package flab.resellPlatform.repository;

import flab.resellPlatform.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//@Repository
public class MemoryUserRepository implements UserRepository {

    private static Map<Long, User> storage = new ConcurrentHashMap<>();
    private static AtomicLong identifier = new AtomicLong();

    @Override
    public User save(User user) {
        user.setId(identifier.incrementAndGet());
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long id, User updatedUser) {
        Optional<User> user = findById(id);
        if (user.isEmpty()) return null;

        updatedUser.setId(id);
        storage.replace(id, updatedUser);
        return updatedUser;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return storage.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

}
