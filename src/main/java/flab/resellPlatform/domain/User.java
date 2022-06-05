package flab.resellPlatform.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class User {

    private Long id;

    @NotBlank
    @Size(min = 6, max = 20)
    private String username;

    @NotBlank
    @Size(min = 8, max = 30)
    private String password;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Size(max = 10)
    private String name;

    @NotBlank
    @Size(min = 4, max = 20)
    private String nickname;

    @Email
    private String email;

    private String shoeSize;

}
