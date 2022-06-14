package flab.resellPlatform.domain.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class UserDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    @NotBlank
    @Email(message = "{email.form}")
    private String email;

    @NotBlank
    private String shoeSize;

}
