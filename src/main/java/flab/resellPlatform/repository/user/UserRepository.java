package flab.resellPlatform.repository.user;

import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.PasswordInquiryForm;
import flab.resellPlatform.domain.user.StrictLoginInfo;
import flab.resellPlatform.domain.user.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserEntity save(UserEntity userEntity);
    Optional<UserEntity> findUser(String username);
    Optional<String> findUsername(String phoneNumber);
    List<UserEntity> findAll();
    int updatePassword(LoginInfo loginInfo);

    int updatePassword(StrictLoginInfo strictLoginInfo);
}
