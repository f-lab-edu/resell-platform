package flab.resellPlatform.repository;

import flab.resellPlatform.domain.User;

public interface UserRepository {

    User save(User user);
    User update(Long id, User user);
    User findById(Long id);

}
