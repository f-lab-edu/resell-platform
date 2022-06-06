package flab.resellPlatform.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String password;
    private String phoneNumber;
    private String name;
    private String nickname;
    private String email;
    private String shoeSize;

}
