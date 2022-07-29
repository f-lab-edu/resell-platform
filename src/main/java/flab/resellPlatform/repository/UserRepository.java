package flab.resellPlatform.repository;

import flab.resellPlatform.domain.User;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

public interface UserRepository {

    User save(User user);
    User update(Long id, User user);
    Optional<User> findById(Long id);

    @Cacheable(key = "#username", value = "user")
    Optional<User> findByUsername(String username);

}
