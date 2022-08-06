package flab.utils;

import flab.resellPlatform.domain.user.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserTestFactory {

    private UserTestFactory() {}

    public static final long DEFAULT_ID = 1l;
    public static final String DEFAULT_USERNAME = "minsuk";
    public static final String DEFAULT_PW = "123";
    public static final String DEFAULT_NICKNAME = "uj";
    public static final String DEFAULT_EMAIL = "a@a.com";
    public static final String DEFAULT_NAME = "ms";
    public static final String DEFAULT_PHONE_NUMBER = "01033331250";
    public static final String DEFAULT_SHOE_SIZE = "275";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static UserDTO.UserDTOBuilder createUserDTOBuilder () {
        return UserDTO.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PW)
                .nickname(DEFAULT_NICKNAME)
                .email(DEFAULT_EMAIL)
                .shoeSize(DEFAULT_SHOE_SIZE)
                .name(DEFAULT_NAME)
                .phoneNumber(DEFAULT_PHONE_NUMBER)
                .role(ROLE_USER);
    }

    static public UserEntity.UserEntityBuilder createUserEntityBuilder () {
        return UserEntity.builder()
                .id(DEFAULT_ID)
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PW)
                .nickname(DEFAULT_NICKNAME)
                .email(DEFAULT_EMAIL)
                .shoeSize(DEFAULT_SHOE_SIZE)
                .name(DEFAULT_NAME)
                .phoneNumber(DEFAULT_PHONE_NUMBER)
                .role(ROLE_USER);
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

    static public PrincipleDetails.PrincipleDetailsBuilder createPrincipleDetailBuilder() {
        return PrincipleDetails.builder()
                .user(createUserEntityBuilder().build());
    }

    public static Authentication createAuthentication(PrincipleDetails principleDetails) {
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return principleDetails.getAuthorities();
            }

            @Override
            public Object getCredentials() {
                return principleDetails.getPassword();
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return principleDetails;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return principleDetails.getUsername();
            }
        };
    }
}
