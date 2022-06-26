package flab.resellPlatform.repository.user;

import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.PasswordInquiryForm;
import flab.resellPlatform.domain.user.StrictLoginInfo;
import flab.resellPlatform.domain.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {


    void save(UserEntity userEntity) throws SQLException;
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findAll();
    int getUsernameCount(String username);
    Optional<String> findUsername(String phoneNumber);
    Optional<LoginInfo> findRequiredInfoForLogin(PasswordInquiryForm inquiryData);
    int updatePassword(LoginInfo loginInfo);
    int updatePasswordSecurely(StrictLoginInfo strictLoginInfo);
}
