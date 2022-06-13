package flab.resellPlatform.data;

import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.domain.user.UserEntity;

public class UserTestFactory {

    private UserTestFactory() {}

    public static UserDTO.UserDTOBuilder createUserDTOBuilder () {
        return UserDTO.builder()
                .username("minsuk")
                .password("a")
                .nickname("uj")
                .email("a@a.com")
                .shoeSize("275")
                .name("ms")
                .phoneNumber("010-3333-1250");
    }

    static public UserEntity.UserEntityBuilder createUserEntityBuilder () {
        return UserEntity.builder()
                .username("minsuk")
                .password("a")
                .nickname("uj")
                .email("a@a.com")
                .shoeSize("275")
                .name("ms")
                .phoneNumber("010-3333-1250");
    }
}
