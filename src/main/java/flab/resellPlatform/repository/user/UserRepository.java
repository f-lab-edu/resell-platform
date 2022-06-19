package flab.resellPlatform.repository.user;

import flab.resellPlatform.domain.user.PasswordInquiryForm;
import flab.resellPlatform.domain.user.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserEntity save(UserEntity userEntity);
    Optional<UserEntity> findUser(String username);
    List<UserEntity> findAll();
}
