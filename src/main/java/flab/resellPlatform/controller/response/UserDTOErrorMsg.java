package flab.resellPlatform.controller.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserDTOErrorMsg {
    private String username;
    private String password;
    private String phoneNumber;
    private String name;
    private String nickname;
    private String email;
    private String shoeSize;
}
