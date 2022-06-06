package flab.resellPlatform.user.repository;

import flab.resellPlatform.user.domain.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserEntity save(UserEntity userEntity);
    Optional<UserEntity> findById(long id);
    List<UserEntity> findAll();
}
