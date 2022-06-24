package flab.resellPlatform.data;

import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.StrictLoginInfo;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.domain.user.UserEntity;

public class UserTestFactory {

    private UserTestFactory() {}

    public static final String DEFAULT_USERNAME = "minsuk";
    public static final String DEFAULT_PW = "123";
    public static final String DEFAULT_NICKNAME = "uj";
    public static final String DEFAULT_EMAIL = "a@a.com";
    public static final String DEFAULT_NAME = "ms";
    public static final String DEFAULT_PHONE_NUMBER = "01033331250";
    public static final String DEFAULT_SHOE_SIZE = "275";

    public static UserDTO.UserDTOBuilder createUserDTOBuilder () {
        return UserDTO.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PW)
                .nickname(DEFAULT_NICKNAME)
                .email(DEFAULT_EMAIL)
                .shoeSize(DEFAULT_SHOE_SIZE)
                .name(DEFAULT_NAME)
                .phoneNumber(DEFAULT_PHONE_NUMBER);
    }

    static public UserEntity.UserEntityBuilder createUserEntityBuilder () {
        return UserEntity.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PW)
                .nickname(DEFAULT_NICKNAME)
                .email(DEFAULT_EMAIL)
                .shoeSize(DEFAULT_SHOE_SIZE)
                .name(DEFAULT_NAME)
                .phoneNumber(DEFAULT_PHONE_NUMBER);
    }

    static public LoginInfo.LoginInfoBuilder createLoginInfoBuilder() {
        return LoginInfo.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PW);
    }

    static public StrictLoginInfo.StrictLoginInfoBuilder createStrictLoginInfoBuilder() {
        return StrictLoginInfo.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PW)
                .phoneNumber(DEFAULT_PHONE_NUMBER)
                .email(DEFAULT_EMAIL);
    }
}
