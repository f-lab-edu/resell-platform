package flab.resellPlatform.repository.user;

import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.StrictLoginInfo;
import flab.resellPlatform.domain.user.UserEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /**
    * UserEntity를 DB에 저장한다.
    *
    * @param userEntity
    * @return UserEntity
    * @exception DuplicateKeyException 이미 Username이 존재할 시 발생.
    * @작성일 6/26/2022
    * @작성자 minsuk
    */

    UserEntity save(UserEntity userEntity)  throws DuplicateKeyException;
    @Cacheable(value = "user", key = "#username")
    Optional<UserEntity> findUser(String username);
    Optional<String> findUsername(String phoneNumber);
    List<UserEntity> findAll();
    int updatePassword(LoginInfo loginInfo);

    int updatePassword(StrictLoginInfo strictLoginInfo);
}
