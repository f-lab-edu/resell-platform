package flab.resellPlatform.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private Long id;

    private String username;

    private String password;

    private String phoneNumber;

    private String name;

    private String nickname;

    private String email;

    private String shoeSize;

    private String role;

}
