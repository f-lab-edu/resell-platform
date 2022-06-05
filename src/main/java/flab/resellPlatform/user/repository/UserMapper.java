package flab.resellPlatform.user.repository;

import flab.resellPlatform.user.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    void save(User user);
    Optional<User> findById(long id);
    List<User> findAll();
}
