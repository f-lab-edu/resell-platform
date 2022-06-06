package flab.resellPlatform.user.repository;

import flab.resellPlatform.user.domain.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    void save(UserEntity userEntity);
    Optional<UserEntity> findById(long id);
    List<UserEntity> findAll();
}
