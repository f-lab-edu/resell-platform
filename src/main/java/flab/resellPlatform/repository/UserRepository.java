package flab.resellPlatform.repository;

import flab.resellPlatform.domain.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);
    User update(Long id, User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);

}
