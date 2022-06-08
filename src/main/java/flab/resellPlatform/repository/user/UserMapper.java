package flab.resellPlatform.repository.user;

import flab.resellPlatform.domain.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    void save(UserEntity userEntity);
    Optional<UserEntity> findById(long id);
    List<UserEntity> findAll();
}
