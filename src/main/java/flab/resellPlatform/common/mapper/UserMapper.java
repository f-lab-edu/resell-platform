package flab.resellPlatform.common.mapper;

import flab.resellPlatform.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user(username, password, phoneNumber, name, nickname, email, shoeSize, role) VALUES(#{username}, #{password}, #{phoneNumber}, #{name}, #{nickname}, #{email}, #{shoeSize}, #{role})")
    void save(User user);

    @Update("UPDATE user SET username = #{user.username}, password = #{user.password}, phoneNumber = #{user.phoneNumber}, name = #{user.name}, nickname = #{user.nickname}, email = #{user.email}, shoeSize = #{user.shoeSize} WHERE id = #{id}")
    void update(Long id, User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    Optional<User> findById(Long id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    Optional<User> findByUsername(String username);

}
