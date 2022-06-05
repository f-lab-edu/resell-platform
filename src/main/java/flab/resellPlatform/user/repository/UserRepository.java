package flab.resellPlatform.user.repository;

import flab.resellPlatform.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(long id);
    List<User> findAll();
}
